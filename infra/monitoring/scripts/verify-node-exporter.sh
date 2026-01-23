#!/bin/bash

# Node Exporter 설치 및 메트릭 수집 확인 스크립트

set -e

echo "🔍 Node Exporter 상태 확인..."
echo ""

# 서버 목록
SERVERS=(
    "172.31.32.89:Kafka Server"
    "172.31.46.94:Stress Server"
    "172.31.43.230:Microservice Server"
)

for server_info in "${SERVERS[@]}"; do
    IFS=':' read -r ip name <<< "$server_info"

    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "📡 $name ($ip)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    # 포트 확인
    if nc -zv -w 5 $ip 9100 2>&1 | grep -q "succeeded"; then
        echo "✅ Port 9100: Open"

        # 메트릭 수집 가능 여부 확인
        if curl -s --max-time 5 http://$ip:9100/metrics > /dev/null; then
            echo "✅ Metrics: Available"

            # 주요 메트릭 샘플 확인
            echo ""
            echo "📊 샘플 메트릭:"
            curl -s http://$ip:9100/metrics | grep -E "^node_(cpu|memory|filesystem|network)" | head -n 5
        else
            echo "❌ Metrics: Not accessible"
        fi
    else
        echo "❌ Port 9100: Closed or unreachable"
        echo "   Node Exporter가 설치되지 않았거나 방화벽이 차단 중일 수 있습니다."
    fi

    echo ""
done

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📌 다음 단계:"
echo "   1. 모든 서버에서 ✅ 상태 확인"
echo "   2. Prometheus 타겟 확인: http://localhost:9090/targets"
echo "   3. Grafana 대시보드 Import (ID: 1860)"
