#!/bin/bash

# Node Exporter 설치 스크립트
# 각 서버에서 실행하여 노드 메트릭 수집 설정

set -e

NODE_EXPORTER_VERSION="1.7.0"

echo "🚀 Node Exporter 설치 시작..."

# 1. Node Exporter 다운로드
cd /tmp
wget https://github.com/prometheus/node_exporter/releases/download/v${NODE_EXPORTER_VERSION}/node_exporter-${NODE_EXPORTER_VERSION}.linux-amd64.tar.gz

# 2. 압축 해제
tar xvfz node_exporter-${NODE_EXPORTER_VERSION}.linux-amd64.tar.gz

# 3. 바이너리 복사
sudo cp node_exporter-${NODE_EXPORTER_VERSION}.linux-amd64/node_exporter /usr/local/bin/
sudo chmod +x /usr/local/bin/node_exporter

# 4. 전용 사용자 생성
sudo useradd --no-create-home --shell /bin/false node_exporter || echo "User already exists"

# 5. systemd 서비스 파일 생성
sudo tee /etc/systemd/system/node_exporter.service > /dev/null <<EOF
[Unit]
Description=Node Exporter
Documentation=https://github.com/prometheus/node_exporter
After=network-online.target

[Service]
Type=simple
User=node_exporter
Group=node_exporter
ExecStart=/usr/local/bin/node_exporter \\
    --collector.filesystem.mount-points-exclude='^/(dev|proc|sys|var/lib/docker/.+|var/lib/kubelet/.+)($|/)' \\
    --collector.netclass.ignored-devices='^(veth.*)$' \\
    --collector.netdev.device-exclude='^(veth.*)$'

Restart=always
RestartSec=10s

[Install]
WantedBy=multi-user.target
EOF

# 6. 서비스 시작
sudo systemctl daemon-reload
sudo systemctl enable node_exporter
sudo systemctl start node_exporter

# 7. 상태 확인
echo ""
echo "✅ Node Exporter 설치 완료!"
echo ""
sudo systemctl status node_exporter --no-pager

# 8. 메트릭 확인
echo ""
echo "📊 메트릭 확인:"
curl -s http://localhost:9100/metrics | head -n 20

echo ""
echo "🔥 설치 성공! Prometheus에서 이 서버를 타겟으로 추가할 수 있습니다."
echo "   메트릭 엔드포인트: http://$(hostname -I | awk '{print $1}'):9100/metrics"

# 9. 정리
rm -rf /tmp/node_exporter-${NODE_EXPORTER_VERSION}.linux-amd64*
