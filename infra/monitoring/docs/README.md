# Commerce Monitoring Stack

Prometheus와 Grafana 기반 마이크로서비스 모니터링 스택

## 🏗️ 환경별 구성

### 파일 구조
```
monitoring/
├── docker-compose.yml              # 기본 설정 (공통)
├── docker-compose.override.yml     # 로컬 환경 (자동 적용)
├── docker-compose.prod.yml         # 운영 환경 (명시적 지정)
├── .env.local                      # 로컬 환경 변수
├── .env.prod                       # 운영 환경 변수
├── prometheus/
│   ├── prometheus.local.yml        # 로컬 Prometheus 설정
│   └── prometheus.prod.yml         # 운영 Prometheus 설정
└── grafana/
    └── provisioning/
        └── datasources/
            └── prometheus.yml
```

### 환경별 실행 방법

#### 로컬 개발 환경
```bash
# docker-compose.yml + docker-compose.override.yml 자동 병합
docker-compose up -d

# 명시적으로 로컬 환경 변수 지정
docker-compose --env-file .env.local up -d
```

#### 운영 환경
```bash
# 운영 설정 파일 명시적 지정
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod up -d

# 또는 편의를 위한 스크립트 사용
./deploy-prod.sh
```

## 📋 환경별 차이점

### 로컬 환경 (`docker-compose.override.yml`)
- ✅ 모든 포트를 호스트에 노출 (9090, 3000, 9100)
- ✅ 간단한 인증 정보 (admin/admin)
- ✅ Node Exporter 포함 (로컬 메트릭 수집)
- ✅ 대시보드 개발을 위한 볼륨 마운트
- ✅ `host.docker.internal` 사용으로 호스트 서비스 접근
- ⚠️ 리소스 제한 없음

### 운영 환경 (`docker-compose.prod.yml`)
- 🔒 포트를 127.0.0.1에만 바인딩 (리버스 프록시 사용)
- 🔒 강력한 비밀번호 및 시크릿 키 사용
- 🔒 환경 변수로 민감 정보 관리
- 📊 cAdvisor 추가 (컨테이너 메트릭)
- 📧 SMTP 설정 (알림 기능)
- 🎯 리소스 제한 설정 (CPU/Memory)
- 📝 로그 로테이션 설정
- 🌐 실제 서버 IP 사용 (172.31.x.x)

## 🎯 모니터링 타겟

### 로컬 환경
```yaml
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Node Exporter: http://localhost:9100
- Microservices: host.docker.internal:8081-8089
```

### 운영 환경
```yaml
노드 메트릭:
  - Kafka Server: 172.31.32.89:9100
  - Stress Server: 172.31.46.94:9100
  - Microservice Server: 172.31.43.230:9100

애플리케이션 메트릭 (172.31.43.230):
  - Auth: 8089
  - User: 8081
  - Catalog: 8084
  - Inventory: 8083
  - Order: 8085
  - Payment: 8087
```

## 🚀 빠른 시작

### 1. 환경 변수 설정
```bash
# 로컬 환경
cp .env.local .env

# 운영 환경 (민감 정보 수정 필요!)
cp .env.prod .env
# .env 파일 편집하여 실제 값으로 변경
```

### 2. 사전 요구사항

#### 각 서버에 Node Exporter 설치 (운영 환경)
```bash
# 각 서버(172.31.32.89, 172.31.46.94, 172.31.43.230)에서 실행
wget https://github.com/prometheus/node_exporter/releases/download/v1.7.0/node_exporter-1.7.0.linux-amd64.tar.gz
tar xvfz node_exporter-1.7.0.linux-amd64.tar.gz
cd node_exporter-1.7.0.linux-amd64

# systemd 서비스로 등록
sudo cp node_exporter /usr/local/bin/
sudo useradd --no-create-home --shell /bin/false node_exporter

sudo tee /etc/systemd/system/node_exporter.service > /dev/null <<EOF
[Unit]
Description=Node Exporter
After=network.target

[Service]
User=node_exporter
Group=node_exporter
Type=simple
ExecStart=/usr/local/bin/node_exporter

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl start node_exporter
sudo systemctl enable node_exporter
```

#### Spring Boot 애플리케이션 설정
**build.gradle**:
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

**application.yml**:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,info,metrics
  metrics:
    export:
      prometheus:
        enabled: true
```

### 3. 실행

#### 로컬 환경
```bash
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 상태 확인
docker-compose ps
```

#### 운영 환경
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 또는 배포 스크립트 사용
chmod +x deploy-prod.sh
./deploy-prod.sh
```

### 4. 접속 확인

#### 로컬
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)

#### 운영
- Prometheus: http://your-server-ip:9090 (리버스 프록시를 통해)
- Grafana: https://monitoring.yourdomain.com

## 📊 Grafana 대시보드

### 추천 대시보드 ID
1. **Node Exporter Full**: 1860
2. **Spring Boot 2.1 System Monitor**: 11378
3. **JVM (Micrometer)**: 4701
4. **Docker Container Metrics**: 193
5. **Kafka Exporter Overview**: 7589

### Import 방법
1. Grafana → Dashboards → Import
2. Dashboard ID 입력
3. Prometheus 데이터소스 선택
4. Import

## 🔧 운영 환경 추가 설정

### Nginx 리버스 프록시 설정 예시
```nginx
server {
    listen 80;
    server_name monitoring.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name monitoring.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/monitoring.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/monitoring.yourdomain.com/privkey.pem;

    # Grafana
    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Prometheus (선택적)
    location /prometheus/ {
        proxy_pass http://127.0.0.1:9090/;
        proxy_set_header Host $host;
    }
}
```

### Alertmanager 설정 (선택)
알림이 필요한 경우 `docker-compose.prod.yml`에 추가:
```yaml
  alertmanager:
    image: prom/alertmanager:latest
    container_name: alertmanager
    ports:
      - "127.0.0.1:9093:9093"
    volumes:
      - ./alertmanager/alertmanager.yml:/etc/alertmanager/alertmanager.yml
    restart: unless-stopped
    networks:
      - monitoring
```

## 🛠️ 유용한 명령어

### 설정 리로드
```bash
# Prometheus 설정 리로드 (재시작 없이)
curl -X POST http://localhost:9090/-/reload

# Grafana 프로비저닝 리로드
docker-compose restart grafana
```

### 메트릭 확인
```bash
# Prometheus 타겟 상태
curl http://localhost:9090/api/v1/targets | jq

# 특정 서비스 메트릭 확인
curl http://172.31.43.230:8089/actuator/prometheus
```

### 로그 확인
```bash
# 전체 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f prometheus
docker-compose logs -f grafana

# 최근 100줄
docker-compose logs --tail=100 prometheus
```

### 중지 및 삭제
```bash
# 중지
docker-compose down

# 볼륨 포함 완전 삭제
docker-compose down -v

# 운영 환경
docker-compose -f docker-compose.yml -f docker-compose.prod.yml down
```

## 🔐 보안 체크리스트

- [ ] Grafana 기본 비밀번호 변경
- [ ] `.env.prod` 파일 보안 관리 (버전 관리 제외)
- [ ] Prometheus/Grafana 포트를 외부에 직접 노출하지 않음
- [ ] 리버스 프록시(Nginx/Traefik) 사용 + HTTPS
- [ ] Spring Boot `/actuator` 엔드포인트 보안 설정
- [ ] 방화벽 규칙 설정 (필요한 포트만 오픈)
- [ ] 정기적인 컨테이너 이미지 업데이트

## 📈 성능 최적화

### Prometheus 데이터 보존 기간 조정
```yaml
# docker-compose.prod.yml
command:
  - '--storage.tsdb.retention.time=30d'
  - '--storage.tsdb.retention.size=10GB'
```

### Grafana 캐싱 설정
```yaml
environment:
  - GF_RENDERING_SERVER_URL=http://renderer:8081/render
  - GF_RENDERING_CALLBACK_URL=http://grafana:3000/
```

## 🐛 트러블슈팅

### 타겟이 DOWN 상태
```bash
# 1. 네트워크 연결 확인
ping 172.31.43.230

# 2. 포트 확인
telnet 172.31.43.230 8089

# 3. 방화벽 확인
sudo ufw status
sudo ufw allow 9100/tcp

# 4. 애플리케이션 메트릭 엔드포인트 확인
curl http://172.31.43.230:8089/actuator/prometheus
```

### 로컬 환경에서 host.docker.internal 작동 안함
**Linux 사용자**:
- `prometheus.local.yml`에서 `host.docker.internal` → `172.17.0.1` 변경
- 또는 `--add-host=host.docker.internal:host-gateway` 옵션 추가

### Grafana 대시보드가 데이터를 표시하지 않음
```bash
# 1. Prometheus 데이터소스 연결 확인
# Grafana → Configuration → Data Sources

# 2. Prometheus에서 메트릭 확인
curl http://localhost:9090/api/v1/query?query=up

# 3. 시간 범위 확인 (Grafana 우측 상단)
```

## 📚 추가 자료

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Node Exporter](https://github.com/prometheus/node_exporter)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Docker Compose Override](https://docs.docker.com/compose/extends/)
