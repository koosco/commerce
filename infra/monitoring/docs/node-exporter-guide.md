# Node Exporter 설치 및 설정 가이드

## 📌 Node Exporter란?

Prometheus가 시스템 레벨 메트릭을 수집하기 위해 사용하는 **공식 익스포터**입니다.

### 수집 가능한 메트릭

| 카테고리       | 메트릭 예시                               | 용도                       |
| -------------- | ----------------------------------------- | -------------------------- |
| **CPU**        | `node_cpu_seconds_total`                  | CPU 사용률, 코어별 사용량  |
| **메모리**     | `node_memory_MemAvailable_bytes`          | 메모리 사용량, 캐시, Swap  |
| **디스크**     | `node_disk_io_time_seconds_total`         | 디스크 I/O, IOPS           |
| **네트워크**   | `node_network_receive_bytes_total`        | 트래픽, 패킷 손실          |
| **파일시스템** | `node_filesystem_avail_bytes`             | 디스크 용량, 마운트 포인트 |
| **로드**       | `node_load1`, `node_load5`, `node_load15` | 시스템 부하                |
| **프로세스**   | `node_procs_running`                      | 실행 중인 프로세스 수      |

---

## 🚀 설치 방법

### 방법 1: 자동 설치 스크립트 (권장)

```bash
# 스크립트 다운로드 및 실행
curl -o install-node-exporter.sh https://raw.githubusercontent.com/.../install-node-exporter.sh
chmod +x install-node-exporter.sh
sudo ./install-node-exporter.sh
```

또는 프로젝트에서 직접:

```bash
# 각 서버에서 실행
scp scripts/install-node-exporter.sh user@172.31.32.89:/tmp/
ssh user@172.31.32.89 "sudo bash /tmp/install-node-exporter.sh"

scp scripts/install-node-exporter.sh user@172.31.46.94:/tmp/
ssh user@172.31.46.94 "sudo bash /tmp/install-node-exporter.sh"

scp scripts/install-node-exporter.sh user@172.31.43.230:/tmp/
ssh user@172.31.43.230 "sudo bash /tmp/install-node-exporter.sh"
```

### 방법 2: 수동 설치 (systemd)

```bash
# 1. 다운로드
cd /tmp
wget https://github.com/prometheus/node_exporter/releases/download/v1.7.0/node_exporter-1.7.0.linux-amd64.tar.gz
tar xvfz node_exporter-1.7.0.linux-amd64.tar.gz

# 2. 설치
sudo cp node_exporter-1.7.0.linux-amd64/node_exporter /usr/local/bin/
sudo chmod +x /usr/local/bin/node_exporter

# 3. 사용자 생성
sudo useradd --no-create-home --shell /bin/false node_exporter

# 4. systemd 서비스 생성
sudo vi /etc/systemd/system/node_exporter.service
```

**서비스 파일 내용:**

```ini
[Unit]
Description=Node Exporter
After=network.target

[Service]
Type=simple
User=node_exporter
Group=node_exporter
ExecStart=/usr/local/bin/node_exporter \
    --collector.filesystem.mount-points-exclude='^/(dev|proc|sys|var/lib/docker/.+)($|/)' \
    --collector.netclass.ignored-devices='^(veth.*)$'

Restart=always
RestartSec=10s

[Install]
WantedBy=multi-user.target
```

```bash
# 5. 서비스 시작
sudo systemctl daemon-reload
sudo systemctl enable node_exporter
sudo systemctl start node_exporter

# 6. 상태 확인
sudo systemctl status node_exporter
```

### 방법 3: Docker로 실행

```bash
# 프로젝트의 Docker Compose 파일 사용
docker-compose -f scripts/docker-node-exporter.yml up -d

# 또는 직접 실행
docker run -d \
  --name node-exporter \
  --net="host" \
  --pid="host" \
  -v "/proc:/host/proc:ro" \
  -v "/sys:/host/sys:ro" \
  -v "/:/rootfs:ro" \
  prom/node-exporter:v1.7.0 \
  --path.procfs=/host/proc \
  --path.sysfs=/host/sys \
  --path.rootfs=/rootfs \
  --collector.filesystem.mount-points-exclude='^/(sys|proc|dev|host|etc)($$|/)'
```

**⚠️ Docker 실행 시 주의사항:**

- `network_mode: host` 필수 (포트 9100 노출)
- 호스트 파일시스템 마운트 필요
- 일부 메트릭 수집에 제한이 있을 수 있음

---

## ✅ 설치 확인

### 1. 로컬에서 확인

```bash
# Node Exporter 실행 확인
sudo systemctl status node_exporter

# 메트릭 엔드포인트 확인
curl http://localhost:9100/metrics

# 주요 메트릭 샘플 확인
curl http://localhost:9100/metrics | grep node_cpu
```

### 2. Prometheus에서 확인

```bash
# 타겟 상태 확인 (Prometheus UI)
http://localhost:9090/targets

# 또는 API로 확인
curl http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | select(.labels.job | contains("node"))'
```

### 3. 검증 스크립트 사용

```bash
# 모든 서버의 Node Exporter 상태 일괄 확인
./scripts/verify-node-exporter.sh
```

---

## 🔧 방화벽 설정

Node Exporter는 **포트 9100**을 사용합니다.

### UFW (Ubuntu)

```bash
# Prometheus 서버에서만 접근 허용
sudo ufw allow from <prometheus-server-ip> to any port 9100

# 또는 모든 IP 허용 (비권장)
sudo ufw allow 9100/tcp
```

### firewalld (CentOS/RHEL)

```bash
# 포트 열기
sudo firewall-cmd --permanent --add-port=9100/tcp
sudo firewall-cmd --reload

# 특정 IP만 허용
sudo firewall-cmd --permanent --add-rich-rule='rule family="ipv4" source address="<prometheus-ip>" port port="9100" protocol="tcp" accept'
sudo firewall-cmd --reload
```

### AWS Security Group

```yaml
Type: Custom TCP Rule
Protocol: TCP
Port Range: 9100
Source: <Prometheus 서버의 Security Group ID>
```

---

## 🎯 Prometheus 설정

Node Exporter 설치 후 Prometheus 설정에 타겟 추가:

**prometheus.prod.yml:**

```yaml
scrape_configs:
  - job_name: "node-kafka"
    static_configs:
      - targets: ["172.31.32.89:9100"]
        labels:
          instance: "kafka-server"
          environment: "production"

  - job_name: "node-stress"
    static_configs:
      - targets: ["172.31.46.94:9100"]
        labels:
          instance: "stress-server"
          environment: "production"

  - job_name: "node-microservice"
    static_configs:
      - targets: ["172.31.43.230:9100"]
        labels:
          instance: "microservice-server"
          environment: "production"
```

설정 리로드:

```bash
curl -X POST http://localhost:9090/-/reload
```

---

## 📊 Grafana 대시보드

### 추천 대시보드

1. **Node Exporter Full (ID: 1860)**

   - 가장 인기 있는 종합 대시보드
   - CPU, 메모리, 디스크, 네트워크 모두 포함

2. **Node Exporter for Prometheus Dashboard (ID: 11074)**

   - 심플하고 직관적인 대시보드

3. **Node Exporter Server Metrics (ID: 405)**
   - 서버 모니터링에 최적화

### Import 방법

```
1. Grafana 접속 (http://localhost:3000)
2. 좌측 메뉴 → Dashboards → Import
3. Dashboard ID 입력: 1860
4. Load 클릭
5. Prometheus 데이터소스 선택
6. Import 클릭
```

---

## 🐛 트러블슈팅

### 1. Port 9100이 열리지 않음

```bash
# 프로세스 확인
sudo netstat -tlnp | grep 9100
sudo ss -tlnp | grep 9100

# Node Exporter 실행 확인
ps aux | grep node_exporter
```

### 2. Prometheus에서 타겟이 DOWN

```bash
# 네트워크 연결 확인
ping 172.31.32.89
telnet 172.31.32.89 9100

# 방화벽 확인
sudo ufw status
sudo iptables -L -n | grep 9100

# 로그 확인
sudo journalctl -u node_exporter -f
```

### 3. 메트릭이 수집되지 않음

```bash
# Node Exporter 로그 확인
sudo journalctl -u node_exporter --no-pager -n 100

# Prometheus 설정 확인
curl http://localhost:9090/api/v1/targets | jq

# 수동으로 메트릭 확인
curl http://172.31.32.89:9100/metrics | grep -E "node_(cpu|memory)"
```

### 4. 특정 메트릭이 없음

```bash
# 활성화된 컬렉터 확인
curl http://localhost:9100/metrics | grep "node_exporter_build_info"

# 사용 가능한 모든 컬렉터 확인
/usr/local/bin/node_exporter --help | grep collector
```

---

## 🔄 업데이트 및 관리

### Node Exporter 업데이트

```bash
# 1. 서비스 중지
sudo systemctl stop node_exporter

# 2. 새 버전 다운로드
cd /tmp
wget https://github.com/prometheus/node_exporter/releases/download/v1.8.0/node_exporter-1.8.0.linux-amd64.tar.gz
tar xvfz node_exporter-1.8.0.linux-amd64.tar.gz

# 3. 바이너리 교체
sudo cp node_exporter-1.8.0.linux-amd64/node_exporter /usr/local/bin/

# 4. 서비스 재시작
sudo systemctl start node_exporter
sudo systemctl status node_exporter
```

### 서비스 관리

```bash
# 시작
sudo systemctl start node_exporter

# 중지
sudo systemctl stop node_exporter

# 재시작
sudo systemctl restart node_exporter

# 상태 확인
sudo systemctl status node_exporter

# 로그 확인
sudo journalctl -u node_exporter -f
```

### 제거

```bash
# 서비스 중지 및 비활성화
sudo systemctl stop node_exporter
sudo systemctl disable node_exporter

# 파일 삭제
sudo rm /usr/local/bin/node_exporter
sudo rm /etc/systemd/system/node_exporter.service

# 사용자 삭제
sudo userdel node_exporter

# systemd 리로드
sudo systemctl daemon-reload
```

---

## 📚 참고 자료

- [Node Exporter GitHub](https://github.com/prometheus/node_exporter)
- [Prometheus Documentation](https://prometheus.io/docs/guides/node-exporter/)
- [Grafana Dashboard 1860](https://grafana.com/grafana/dashboards/1860)

---

## ✅ 체크리스트

설치 완료 후 확인:

- [ ] Node Exporter 서비스 실행 중 (`systemctl status node_exporter`)
- [ ] 포트 9100 리스닝 (`netstat -tlnp | grep 9100`)
- [ ] 메트릭 엔드포인트 접근 가능 (`curl http://localhost:9100/metrics`)
- [ ] 방화벽 규칙 설정 (`ufw allow 9100/tcp`)
- [ ] Prometheus 타겟 추가 및 UP 상태 확인
- [ ] Grafana 대시보드 Import 완료
