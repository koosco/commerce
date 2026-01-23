#!/bin/bash

# 운영 환경 배포 스크립트

set -e  # 에러 발생 시 중단

echo "🚀 Starting production deployment..."

# 환경 변수 파일 확인
if [ ! -f .env.prod ]; then
    echo "❌ .env.prod file not found!"
    echo "Please copy .env.prod.example to .env.prod and configure it."
    exit 1
fi

# 민감 정보 확인
if grep -q "CHANGE_ME" .env.prod; then
    echo "⚠️  Warning: .env.prod contains default values!"
    echo "Please update sensitive information before deploying."
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 기존 컨테이너 중지 및 제거
echo "📦 Stopping existing containers..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml down

# 최신 이미지 pull
echo "🔄 Pulling latest images..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml pull

# 컨테이너 시작
echo "🚢 Starting containers..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod up -d

# 상태 확인
echo "⏳ Waiting for services to be ready..."
sleep 10

echo "📊 Checking container status..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml ps

# Health check
echo "🏥 Health check..."
if curl -s http://localhost:9090/-/ready > /dev/null 2>&1; then
    echo "✅ Prometheus is ready"
else
    echo "❌ Prometheus health check failed"
fi

if curl -s http://localhost:3000/api/health > /dev/null 2>&1; then
    echo "✅ Grafana is ready"
else
    echo "❌ Grafana health check failed"
fi

echo "✅ Deployment completed!"
echo ""
echo "📝 Access URLs:"
echo "  - Prometheus: http://localhost:9090 (or via reverse proxy)"
echo "  - Grafana: http://localhost:3000 (or via reverse proxy)"
echo ""
echo "📋 Useful commands:"
echo "  - View logs: docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f"
echo "  - Stop: docker-compose -f docker-compose.yml -f docker-compose.prod.yml down"
echo "  - Restart: docker-compose -f docker-compose.yml -f docker-compose.prod.yml restart"
