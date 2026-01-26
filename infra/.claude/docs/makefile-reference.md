# Makefile 참조

이 문서는 인프라 관리를 위한 Makefile 명령어 레퍼런스를 제공합니다.

## 명령 카테고리

| 카테고리 | 명령어 | 목적 |
|---------|--------|------|
| **네임스페이스** | `k8s-ns-create`, `k8s-ns-delete` | 네임스페이스 라이프사이클 |
| **리소스** | `k8s-apply-all`, `k8s-services-apply` | 리소스 배포 |
| **배포** | `k8s-start`, `k8s-stop`, `k8s-restart`, `k8s-scale` | 서비스 작업 |
| **Ingress** | `k8s-ingress-apply`, `k8s-ingress-list` | Ingress 관리 |
| **모니터링** | `k8s-status`, `k8s-deployments` | 상태 확인 |
| **로컬 개발** | `k8s-traefik-ip`, `k8s-port-forward` | 로컬 접근 |

## 일반적인 워크플로우

### 초기 배포

```bash
make k8s-ns-create
make k8s-apply-all ENV=dev
make k8s-start
make k8s-status
```

### 서비스 업데이트

```bash
make k8s-services-apply
make k8s-restart
```

### 서비스 스케일링

```bash
make k8s-scale REPLICAS=5
```

### 정상 종료

```bash
make k8s-stop
```

### 완전 삭제

```bash
make k8s-ns-delete
```

## 명령어 상세

### 네임스페이스 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-ns-create` | commerce 네임스페이스 생성 |
| `k8s-ns-delete` | commerce 네임스페이스 삭제 (모든 리소스 포함) |

### 리소스 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-apply-all ENV=<env>` | 모든 리소스 적용 (ConfigMap, Secret, Services, Ingress) |
| `k8s-services-apply` | 서비스 Deployment만 적용 |

### 배포 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-start` | 모든 서비스 시작 (replicas > 0) |
| `k8s-stop` | 모든 서비스 중지 (replicas = 0) |
| `k8s-restart` | 모든 서비스 롤링 재시작 |
| `k8s-scale REPLICAS=<n>` | 모든 서비스를 n개 복제본으로 스케일링 |

### Ingress 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-ingress-apply ENV=<env>` | Ingress 리소스 적용 |
| `k8s-ingress-list` | Ingress 상태 조회 |

### 모니터링 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-status` | 전체 상태 확인 (pods, services, ingress) |
| `k8s-deployments` | Deployment 상태 확인 |

### 로컬 개발 명령어

| 명령어 | 설명 |
|--------|------|
| `k8s-traefik-ip` | Traefik Ingress IP 조회 |
| `k8s-port-forward` | 로컬 포트 포워딩 설정 |

## 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `ENV` | dev | 환경 (dev, prod) |
| `NAMESPACE` | commerce | Kubernetes 네임스페이스 |
| `REPLICAS` | 2 | 스케일링 시 복제본 수 |

## 사용 예시

```bash
# 개발 환경 배포
make k8s-apply-all ENV=dev

# 운영 환경 배포
make k8s-apply-all ENV=prod

# 특정 복제본 수로 스케일링
make k8s-scale REPLICAS=3

# Ingress 적용 (개발)
make k8s-ingress-apply ENV=dev
```
