# k8s 클러스터 내 nginx로 Scalar API 문서 통합 배포하기

## 문제 정의

분산 시스템을 개발하다 보면 서비스가 늘어날수록 API 문서 관리가 점점 번거로워집니다. 이 프로젝트에는 Auth, User, Catalog, Inventory, Order, Payment 총 6개의 서비스가 존재하며, 각 서비스가 Spring Boot의 springdoc-openapi를 통해 `/v3/api-docs` 엔드포인트를 노출하고 있습니다.

문제는 이 API 문서를 확인하려면 각 서비스에 개별적으로 접근해야 한다는 점입니다. 포트포워딩을 걸거나 각 서비스 URL을 일일이 기억해야 했고, 외부에서 접근하려면 더 복잡해집니다. 포트폴리오 프로젝트 특성상 API 문서를 한 곳에서 깔끔하게 보여줄 수 있는 통합 문서 페이지가 필요했습니다.

요구사항을 정리하면 다음과 같습니다.

- 하나의 URL(`/docs`)에서 6개 서비스의 API 문서를 모두 확인할 수 있을 것
- 서비스 간 전환이 간편할 것
- 기존 인프라(Cloudflare Tunnel, Traefik, k3s) 변경을 최소화할 것
- 커스텀 Docker 이미지 빌드 없이 배포할 것

## 대안책

### 1. 각 서비스에 Swagger UI 내장

Spring Boot 서비스마다 `springdoc-openapi-ui` 의존성을 추가하여 Swagger UI를 내장하는 방법입니다. 가장 단순하지만 6개 서비스를 개별적으로 접근해야 하므로 "통합 문서"라는 목적에 맞지 않습니다.

### 2. Spring Cloud Gateway에서 OpenAPI 통합

Spring Cloud Gateway가 각 서비스의 OpenAPI 스펙을 수집하여 하나의 Swagger UI로 제공하는 방법입니다. 이 프로젝트는 서비스 간 통신에 Kafka만 사용하고 API Gateway를 두지 않는 구조이므로, API 문서만을 위해 Gateway를 도입하는 것은 과도합니다.

### 3. nginx 리버스 프록시 + Scalar UI (선택)

k8s 내에 경량 nginx 컨테이너를 하나 배포하여, 정적 HTML(Scalar UI)을 서빙하면서 동시에 각 서비스의 `/v3/api-docs`를 리버스 프록시하는 방법입니다.

이 방법을 선택한 이유는 다음과 같습니다.

- **커스텀 빌드 불필요**: `nginx:1.27-alpine` 이미지를 그대로 사용하고, ConfigMap으로 설정만 주입하면 됩니다
- **리소스 절약**: 메모리 32~64Mi, CPU 10~100m으로 충분히 동작합니다
- **기존 인프라 변경 최소화**: Ingress에 `/docs` 경로 하나만 추가하면 됩니다
- **Scalar UI**: Swagger UI 대비 현대적인 디자인과 "Try it out" 기능을 제공합니다

## 해결 과정

### 트래픽 흐름 설계

먼저 전체 트래픽 흐름을 설계합니다. 기존에 Cloudflare Tunnel → Traefik → k8s Ingress → 각 서비스로 이어지는 경로가 구축되어 있으므로, 여기에 `/docs` 경로만 추가하면 됩니다.

```
브라우저
  → Cloudflare Tunnel
    → cloudflared (127.0.0.1:9080)
      → Traefik (k3s)
        → Ingress (/docs)
          → docs-service (nginx)
              ├── /docs/          → index.html (Scalar UI)
              └── /docs/api/{svc}/v3/api-docs → {svc}-service:80 (클러스터 내부)
```

핵심은 nginx가 두 가지 역할을 동시에 수행한다는 점입니다. 정적 파일 서빙(Scalar UI HTML)과 각 서비스의 OpenAPI 엔드포인트에 대한 리버스 프록시입니다.

### 단계 1: nginx 설정 (ConfigMap)

nginx 설정에서 주의해야 할 부분이 두 가지 있습니다.

첫째, **DNS resolver 설정**입니다. k8s 클러스터 내에서 서비스 이름으로 다른 Pod에 접근하려면 kube-dns를 resolver로 지정해야 합니다.

```nginx
resolver kube-dns.kube-system.svc.cluster.local valid=30s;
```

둘째, **변수를 통한 proxy_pass**입니다. nginx는 설정 로드 시점에 upstream을 resolve하는데, k8s 서비스가 아직 존재하지 않으면 nginx가 시작에 실패합니다. `set $upstream` 변수를 사용하면 요청 시점에 resolve하므로 이 문제를 회피할 수 있습니다.

```nginx
# 잘못된 방법 - nginx 시작 시 resolve 시도, 서비스 없으면 실패
location = /docs/api/catalog/v3/api-docs {
    proxy_pass http://catalog-service.commerce.svc.cluster.local/v3/api-docs;
}

# 올바른 방법 - 요청 시점에 resolve
location = /docs/api/catalog/v3/api-docs {
    set $upstream http://catalog-service.commerce.svc.cluster.local;
    proxy_pass $upstream/v3/api-docs;
}
```

전체 nginx 설정은 다음과 같습니다.

```nginx
resolver kube-dns.kube-system.svc.cluster.local valid=30s;

server {
    listen 80;
    root /usr/share/nginx/html;

    location = /docs { return 301 /docs/; }

    location /docs/ {
        alias /usr/share/nginx/html/;
        try_files $uri /index.html;
    }

    location = /docs/api/auth/v3/api-docs {
        set $upstream http://auth-service.commerce.svc.cluster.local;
        proxy_pass $upstream/v3/api-docs;
    }

    location = /docs/api/users/v3/api-docs {
        set $upstream http://user-service.commerce.svc.cluster.local;
        proxy_pass $upstream/v3/api-docs;
    }

    location = /docs/api/catalog/v3/api-docs {
        set $upstream http://catalog-service.commerce.svc.cluster.local;
        proxy_pass $upstream/v3/api-docs;
    }

    location = /docs/api/inventory/v3/api-docs {
        set $upstream http://inventory-service.commerce.svc.cluster.local;
        proxy_pass $upstream/v3/api-docs;
    }

    location = /docs/api/orders/v3/api-docs {
        set $upstream http://order-service.commerce.svc.cluster.local;
        proxy_pass $upstream/v3/api-docs;
    }

    location = /docs/api/payments/v3/api-docs {
        set $upstream http://payment-service.commerce.svc.cluster.local;
        proxy_pass $upstream/v3/api-docs;
    }

    location /healthz { return 200 'ok'; add_header Content-Type text/plain; }
}
```

6개 서비스 각각에 대해 `exact match`(`location =`)로 프록시 경로를 정의했습니다. 이렇게 하면 의도하지 않은 경로가 프록시되는 것을 방지할 수 있습니다.

### 단계 2: Scalar UI HTML (ConfigMap)

Scalar는 CDN에서 제공하는 JavaScript 한 줄로 API 문서 UI를 렌더링할 수 있습니다. 여기에 서비스 선택 드롭다운을 추가하여, 하나의 페이지에서 서비스를 전환할 수 있게 합니다.

```html
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Commerce API Documentation</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        .service-selector {
            position: fixed; top: 12px; right: 16px; z-index: 1000;
            display: flex; align-items: center; gap: 8px;
        }
        .service-selector select {
            padding: 6px 12px; border-radius: 6px;
            border: 1px solid #555; background: #2a2a2a; color: #eee;
        }
    </style>
</head>
<body>
    <div class="service-selector">
        <label for="service-select">Service:</label>
        <select id="service-select">
            <option value="/docs/api/auth/v3/api-docs">Auth</option>
            <option value="/docs/api/users/v3/api-docs">User</option>
            <option value="/docs/api/catalog/v3/api-docs" selected>Catalog</option>
            <option value="/docs/api/inventory/v3/api-docs">Inventory</option>
            <option value="/docs/api/orders/v3/api-docs">Order</option>
            <option value="/docs/api/payments/v3/api-docs">Payment</option>
        </select>
    </div>

    <div id="scalar-root"></div>

    <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
    <script>
        const select = document.getElementById('service-select');

        function renderDocs(url) {
            const root = document.getElementById('scalar-root');
            root.innerHTML = '';
            Scalar.createApiReference(root, {
                url: url,
                darkMode: true,
                servers: [
                    { url: 'https://commerce.koomango.com', description: 'Production' }
                ]
            });
        }

        select.addEventListener('change', function () {
            renderDocs(this.value);
        });

        renderDocs(select.value);
    </script>
</body>
</html>
```

`servers` 옵션에 프로덕션 도메인을 지정하여, Scalar의 "Try it out" 기능에서 바로 실제 API를 호출할 수 있게 했습니다.

### 단계 3: Deployment와 Service

nginx 설정과 HTML을 각각 ConfigMap으로 정의했으므로, Deployment에서는 이를 볼륨으로 마운트하기만 하면 됩니다.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: docs-service
  namespace: commerce
spec:
  replicas: 1
  selector:
    matchLabels:
      app: docs-service
  template:
    spec:
      containers:
        - name: nginx
          image: nginx:1.27-alpine
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 80
          resources:
            requests:
              memory: "32Mi"
              cpu: "10m"
            limits:
              memory: "64Mi"
              cpu: "100m"
          livenessProbe:
            httpGet:
              path: /healthz
              port: 80
          readinessProbe:
            httpGet:
              path: /healthz
              port: 80
          volumeMounts:
            - name: nginx-config
              mountPath: /etc/nginx/conf.d/default.conf
              subPath: default.conf
            - name: html
              mountPath: /usr/share/nginx/html/index.html
              subPath: index.html
      volumes:
        - name: nginx-config
          configMap:
            name: docs-nginx-config
        - name: html
          configMap:
            name: docs-html
```

커스텀 Docker 이미지를 빌드하지 않고, 공식 `nginx:1.27-alpine` 이미지에 ConfigMap만 마운트하는 구조입니다. `subPath`를 사용하여 디렉토리가 아닌 개별 파일 단위로 마운트한 점에 주목해 주세요. 이렇게 하면 nginx의 기존 기본 파일들을 덮어쓰지 않으면서 필요한 파일만 교체할 수 있습니다.

### 단계 4: Ingress 경로 추가

기존 Ingress에 `/docs` 경로를 추가합니다. 다른 서비스 경로(`/api/*`)보다 앞에 배치합니다.

```yaml
spec:
  rules:
    - host: commerce.koomango.com
      http:
        paths:
          # API Documentation - Scalar UI
          - path: /docs
            pathType: Prefix
            backend:
              service:
                name: docs-service
                port:
                  number: 80

          # Auth Service
          - path: /api/auth
            pathType: Prefix
            backend:
              service:
                name: auth-service
                port:
                  number: 80
          # ... 나머지 서비스 경로
```

`/docs` 경로는 `/api/*` 경로와 겹치지 않으므로, 기존 Traefik Middleware(CORS, Rate Limit, Security Headers)가 적용되더라도 문서 페이지 접근에 영향을 주지 않습니다.

### 배포

모든 리소스를 하나의 YAML 파일(`docs-service.yaml`)에 정의했으므로, 배포는 두 개의 명령으로 완료됩니다.

```bash
kubectl apply -f infra/k8s/services/docs-service.yaml
kubectl apply -f infra/k8s/ingress.yaml
```

## 결과

이제 `https://commerce.koomango.com/docs/`에 접속하면 Scalar UI 기반의 통합 API 문서 페이지가 표시됩니다. 우측 상단의 드롭다운으로 서비스를 전환할 수 있고, "Try it out" 기능으로 실제 API를 호출할 수 있습니다.

구현 결과를 정리하면 다음과 같습니다.

- **변경한 파일**: 1개 생성(`docs-service.yaml`), 1개 수정(`ingress.yaml`)
- **커스텀 이미지 빌드**: 불필요 (공식 nginx:1.27-alpine 사용)
- **리소스 사용량**: 메모리 32~64Mi, CPU 10~100m
- **기존 인프라 변경**: 없음 (Cloudflare Tunnel, cloudflared, 호스트 nginx 모두 그대로)

이 접근 방식에서 얻은 인사이트는 다음과 같습니다.

- ConfigMap을 활용하면 커스텀 Docker 이미지 빌드 없이도 nginx의 설정과 정적 파일을 유연하게 관리할 수 있습니다
- nginx에서 k8s 내부 서비스를 프록시할 때는 반드시 `resolver` 설정과 `set $upstream` 패턴을 사용해야 합니다. 그렇지 않으면 서비스 시작 순서에 따라 nginx가 기동에 실패할 수 있습니다
- Scalar UI는 CDN 스크립트 한 줄로 동작하므로, 별도의 빌드 파이프라인 없이 ConfigMap에 HTML을 직접 넣는 것만으로 충분합니다

추후 개선할 수 있는 부분으로는 서비스가 추가될 때 ConfigMap의 nginx 설정과 HTML 드롭다운을 수동으로 업데이트해야 한다는 점이 있습니다. 서비스 목록을 동적으로 가져오는 방식(예: k8s API를 활용한 서비스 디스커버리)으로 발전시킬 수 있겠지만, 현재 6개 서비스 규모에서는 정적 설정으로 충분합니다.
