# Troubleshooting Guide

## 🐛 자주 발생하는 문제 해결

### 1. 볼륨 마운트 에러

#### 에러 메시지

```
Error response from daemon: failed to create task for container:
failed to create shim task: OCI runtime create failed:
runc create failed: unable to start container process:
error during container init: error mounting "/host_mnt/Users/.../prometheus.local.yml"
to rootfs at "/etc/prometheus/prometheus.yml"
```

#### 원인

Docker Desktop for Mac에서 특정 파일을 직접 마운트할 때 발생하는 문제

#### 해결 방법 ✅

**디렉토리 전체를 마운트하고, command로 설정 파일 지정**

**수정 전 (잘못됨):**

```yaml
services:
  prometheus:
    volumes:
      - ./prometheus/prometheus.local.yml:/etc/prometheus/prometheus.yml # ❌
```

**수정 후 (올바름):**

```yaml
services:
  prometheus:
    # docker-compose.yml에서 이미 디렉토리 전체 마운트
    # volumes:
    #   - ./prometheus:/etc/prometheus

    # override에서는 command로 설정 파일만 지정
    command:
      - "--config.file=/etc/prometheus/prometheus.local.yml" # ✅
```

#### 적용 방법

```bash
# 1. 기존 컨테이너 중지 및 제거
docker-compose down

# 2. 볼륨까지 완전 삭제 (선택)
docker-compose down -v

# 3. 다시 시작
docker-compose up -d

# 4. 로그 확인
docker-compose logs -f prometheus
```

---

### 2. Prometheus 타겟이 DOWN 상태

#### 증상

```
Prometheus UI → Status → Targets
모든 타겟이 DOWN 또는 UNKNOWN
```

#### 원인

1. 네트워크 연결 문제
2. 방화벽 차단
3. 서비스가 실제로 실행되지 않음
4. 메트릭 엔드포인트 경로 오류

#### 해결 방법

**Step 1: 네트워크 연결 확인**

```bash
# 타겟 서버로 ping
ping 172.31.43.230

# 포트 확인
telnet 172.31.43.230 8089
# 또는
nc -zv 172.31.43.230 8089
```

**Step 2: 메트릭 엔드포인트 직접 확인**

```bash
# 로컬에서 확인
curl http://172.31.43.230:8089/actuator/prometheus

# 응답 예시 (정상):
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
# jvm_memory_used_bytes{area="heap",id="PS Eden Space",} 1.234567E8
```

**Step 3: 방화벽 확인 및 해제**

```bash
# Ubuntu/Debian
sudo ufw status
sudo ufw allow 8089/tcp
sudo ufw allow 9100/tcp  # Node Exporter

# CentOS/RHEL
sudo firewall-cmd --list-all
sudo firewall-cmd --permanent --add-port=8089/tcp
sudo firewall-cmd --reload
```

**Step 4: Prometheus 설정 확인**

```bash
# Prometheus 컨테이너 내부에서 설정 확인
docker exec prometheus cat /etc/prometheus/prometheus.local.yml

# 설정 리로드
curl -X POST http://localhost:9090/-/reload
```

---

### 3. Grafana "No data" 표시

#### 증상

대시보드에서 모든 패널에 "No data" 또는 "N/A" 표시

#### 원인

1. Prometheus 데이터소스 연결 실패
2. 메트릭 수집 실패
3. 쿼리 오류
4. 시간 범위 문제

#### 해결 방법

**Step 1: 데이터소스 연결 확인**

```
Grafana → Configuration → Data Sources → Prometheus
- HTTP URL: http://prometheus:9090
- Access: Server (default)
- "Save & Test" 클릭 → "Data source is working" 확인
```

**Step 2: Prometheus에서 메트릭 확인**

```bash
# Prometheus UI에서 쿼리 테스트
http://localhost:9090/graph
Query: up
Execute 클릭

# API로 확인
curl http://localhost:9090/api/v1/query?query=up
```

**Step 3: Grafana 쿼리 확인**

```
패널 Edit → Query inspector → Refresh
에러 메시지 확인
```

**Step 4: 시간 범위 조정**

```
Grafana 우측 상단 시간 선택기
- Last 5 minutes → Last 1 hour로 변경
- 또는 Absolute time range로 데이터 존재 시점 선택
```

---

### 4. Docker Compose 실행 실패

#### 에러: "yaml: line X: mapping values are not allowed in this context"

**원인**: YAML 문법 오류 (들여쓰기, 콜론 등)

**해결**:

```bash
# YAML 문법 검증
yamllint docker-compose.yml

# 또는 온라인 검증
# https://www.yamllint.com/
```

#### 에러: "network monitoring declared as external, but could not be found"

**원인**: 네트워크가 생성되지 않음

**해결**:

```bash
# 네트워크 수동 생성
docker network create monitoring

# 또는 external: false로 변경
```

---

### 5. 컨테이너가 계속 재시작됨

#### 증상

```bash
docker-compose ps
# prometheus   Up 5 seconds (health: starting)
# prometheus   Restarting (1) 10 seconds ago
```

#### 원인 확인

```bash
# 로그 확인
docker-compose logs prometheus

# 일반적인 원인:
# - 설정 파일 문법 오류
# - 포트 충돌
# - 권한 문제
```

#### 해결 방법

**설정 파일 검증**

```bash
# Prometheus 설정 검증
docker run --rm -v $(pwd)/prometheus:/etc/prometheus \
  prom/prometheus:latest \
  promtool check config /etc/prometheus/prometheus.local.yml
```

**포트 충돌 확인**

```bash
# 9090 포트 사용 중인 프로세스 확인
lsof -i :9090
# 또는
netstat -an | grep 9090

# 프로세스 종료
kill <PID>
```

---

### 6. 메모리 부족 에러

#### 에러: "Out of memory" 또는 컨테이너가 갑자기 종료됨

#### 해결 방법

**Docker Desktop 메모리 증가**

```
Docker Desktop → Settings → Resources → Memory
4GB → 8GB 이상으로 증가
```

**Prometheus 메모리 제한 조정**

```yaml
# docker-compose.prod.yml
deploy:
  resources:
    limits:
      memory: 4G # 증가
```

**데이터 보존 기간 감소**

```yaml
command:
  - "--storage.tsdb.retention.time=7d" # 30d → 7d
  - "--storage.tsdb.retention.size=5GB" # 추가
```

---

### 7. 로컬 환경에서 host.docker.internal 작동 안 함

#### 증상 (Linux)

```
prometheus.local.yml 에서:
targets: ['host.docker.internal:8089']
→ connection refused
```

#### 원인

`host.docker.internal`은 macOS/Windows에서만 작동

#### 해결 (Linux)

**방법 1: Docker 브리지 게이트웨이 IP 사용**

```yaml
# prometheus.local.yml
targets: ["172.17.0.1:8089"] # Linux 기본 Docker 게이트웨이
```

**방법 2: docker-compose.yml에 extra_hosts 추가**

```yaml
services:
  prometheus:
    extra_hosts:
      - "host.docker.internal:host-gateway" # Docker 20.10+
```

**방법 3: 호스트 네트워크 모드 사용**

```yaml
services:
  prometheus:
    network_mode: "host"
    # 주의: 포트 매핑이 무시됨
```

---

### 8. Grafana 플러그인 설치 실패

#### 에러: "Failed to install plugin"

#### 해결 방법

**환경 변수로 설치**

```yaml
# docker-compose.override.yml
grafana:
  environment:
    - GF_INSTALL_PLUGINS=grafana-piechart-panel,grafana-clock-panel
```

**컨테이너 내부에서 수동 설치**

```bash
docker exec -it grafana grafana-cli plugins install grafana-piechart-panel
docker-compose restart grafana
```

**인터넷 연결 문제 시**

```
Grafana → Configuration → Plugins → Add panel
대신 대시보드 JSON Import 사용
```

---

### 9. 권한 문제 (Permission Denied)

#### 에러: "permission denied" 또는 "cannot create directory"

#### 해결 방법

**볼륨 디렉토리 권한 수정**

```bash
# Prometheus 데이터 디렉토리
sudo chown -R 65534:65534 ./prometheus/data

# Grafana 데이터 디렉토리
sudo chown -R 472:472 ./grafana/data
```

**또는 rootless 모드 사용**

```yaml
services:
  prometheus:
    user: "65534:65534" # nobody 사용자
  grafana:
    user: "472:472" # grafana 사용자
```

---

### 10. SSL/TLS 인증서 문제

#### 에러: "x509: certificate signed by unknown authority"

#### 해결 방법 (자체 서명 인증서 사용 시)

**Prometheus 설정**

```yaml
scrape_configs:
  - job_name: "service"
    scheme: https
    tls_config:
      insecure_skip_verify: true # 개발 환경에서만!
```

**운영 환경에서는 올바른 인증서 사용**

```yaml
tls_config:
  ca_file: /etc/prometheus/ca.crt
  cert_file: /etc/prometheus/client.crt
  key_file: /etc/prometheus/client.key
```

---

## 🛠️ 디버깅 도구

### 유용한 명령어 모음

```bash
# 컨테이너 상태 확인
docker-compose ps

# 실시간 로그 확인
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f prometheus

# 컨테이너 내부 접속
docker exec -it prometheus sh

# 네트워크 확인
docker network ls
docker network inspect monitoring

# 볼륨 확인
docker volume ls
docker volume inspect monitoring_prometheus-data

# 리소스 사용량
docker stats

# 설정 검증
docker-compose config

# 완전 초기화 (주의!)
docker-compose down -v --remove-orphans
docker system prune -a --volumes
```

### Prometheus 헬스체크

```bash
# Readiness 확인
curl http://localhost:9090/-/ready

# Liveness 확인
curl http://localhost:9090/-/healthy

# 설정 리로드
curl -X POST http://localhost:9090/-/reload
```

### Grafana API

```bash
# 헬스체크
curl http://localhost:3000/api/health

# 데이터소스 목록
curl -u admin:admin http://localhost:3000/api/datasources

# 대시보드 목록
curl -u admin:admin http://localhost:3000/api/search
```

---

## 📋 체크리스트

문제 발생 시 순서대로 확인:

### 기본 확인

- [ ] Docker Desktop 실행 중
- [ ] 충분한 디스크 공간 (최소 10GB)
- [ ] 충분한 메모리 (권장 8GB)
- [ ] 포트 충돌 없음 (9090, 3000, 9100)

### 설정 확인

- [ ] YAML 문법 검증
- [ ] 파일 경로 올바름 (절대 경로)
- [ ] 환경 변수 설정됨
- [ ] 네트워크 설정 올바름

### 서비스 확인

- [ ] 모든 타겟 서버 접근 가능
- [ ] Node Exporter 실행 중
- [ ] 애플리케이션 메트릭 엔드포인트 응답
- [ ] 방화벽 규칙 설정됨

### Prometheus 확인

- [ ] 타겟 UP 상태
- [ ] 메트릭 수집 중
- [ ] 설정 파일 로드됨
- [ ] 저장소 공간 충분

### Grafana 확인

- [ ] 데이터소스 연결됨
- [ ] 대시보드 로드됨
- [ ] 쿼리 정상 작동
- [ ] 시간 범위 적절함

---

## 🆘 도움 요청 시 포함할 정보

문제 해결을 위해 다음 정보를 수집하여 공유:

```bash
# 1. 환경 정보
docker --version
docker-compose --version
uname -a

# 2. 컨테이너 상태
docker-compose ps

# 3. 로그 (최근 100줄)
docker-compose logs --tail=100 > logs.txt

# 4. 설정 파일
docker-compose config > config.yml

# 5. 에러 메시지
# 전체 에러 메시지 복사

# 6. 네트워크 정보
docker network inspect monitoring > network.json

# 7. 타겟 상태 (Prometheus)
curl http://localhost:9090/api/v1/targets > targets.json
```

---

## 📚 추가 리소스

- [Docker Compose 트러블슈팅](https://docs.docker.com/compose/troubleshooting/)
- [Prometheus FAQ](https://prometheus.io/docs/introduction/faq/)
- [Grafana Troubleshooting](https://grafana.com/docs/grafana/latest/troubleshooting/)
