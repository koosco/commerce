---
name: mono-docker
description: 로컬 개발 환경 Docker 명령어 가이드. DB, Redis, Kafka 컨테이너 실행, 모니터링 스택 실행이 필요할 때 사용합니다.
---

## 디렉토리 구조

```
infra/
├── docker-compose.yaml    # 통합 Docker Compose (프로필 기반)
├── .env.local             # 로컬 환경 변수
├── .env.dev               # k3d 개발 환경 변수
├── makefiles/docker.mk    # Docker Compose 명령어
├── config/                # 설정 파일
│   ├── redis/
│   ├── prometheus/
│   └── grafana/
└── data/                  # 볼륨 데이터 (gitignored)
```

## Quick Reference (Makefile)

```bash
cd infra

# 핵심 스택 (DB + Redis)
make docker-core

# Kafka 스택 추가
make docker-kafka

# 모니터링 스택 추가
make docker-monitoring

# 전체 스택
make docker-full

# 환경별 실행
make docker-local    # 로컬 환경 전체
make docker-dev      # k3d 개발 환경

# 상태 확인
make docker-ps
make docker-health

# 로그
make docker-logs SERVICE=kafka

# 중지
make docker-down
```

## 프로필 기반 실행

| 프로필 | 서비스 | 용도 |
|--------|--------|------|
| `core` | MariaDB, Redis | 기본 개발 |
| `kafka` | Kafka, Debezium, Kafka UI | 이벤트 기반 개발 |
| `monitoring` | Prometheus, Grafana, Node Exporter | 메트릭 수집 |
| `full` | 모든 서비스 | 통합 테스트 |

```bash
# 직접 docker compose 사용
docker compose --env-file .env.local --profile core up -d
docker compose --env-file .env.local --profile core --profile kafka up -d
docker compose --env-file .env.local --profile full up -d
```

## 서비스 접속 정보

| 서비스 | 접속 정보 |
|--------|----------|
| MariaDB | `localhost:3306` (admin/admin1234) |
| Redis | `localhost:6379` |
| Kafka | `localhost:9092` |
| Kafka UI | `http://localhost:18080` |
| Debezium | `localhost:18083` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3000` (admin/admin123) |

## 데이터베이스

| Service | Database |
|---------|----------|
| auth-service | commerce-auth |
| user-service | commerce-user |
| catalog-service | commerce-catalog |
| inventory-service | commerce-inventory |
| order-service | commerce-order |
| payment-service | commerce-payment |

## 데이터베이스 접속

```bash
# MariaDB 접속
docker exec -it commerce-mariadb mysql -u admin -padmin1234

# 특정 데이터베이스 선택
docker exec -it commerce-mariadb mysql -u admin -padmin1234 commerce-order

# Redis CLI 접속
docker exec -it commerce-redis redis-cli
```

## Kafka 명령어

```bash
# 토픽 목록
make kafka-topics

# 토픽 생성
make kafka-topic-create TOPIC=order-created

# 토픽 삭제
make kafka-topic-delete TOPIC=test-topic

# 컨슈머 그룹 목록
make kafka-consumer-groups
```

## 서비스 Docker 이미지 빌드

```bash
# 1. JAR 빌드 먼저
./gradlew :services:auth-service:bootJar

# 2. Docker 이미지 빌드
cd services/auth-service
docker build -t auth-service:latest .

# 한 줄로
./gradlew :services:auth-service:bootJar && \
docker build -t auth-service:latest services/auth-service/
```

## 볼륨 관리

```bash
# 데이터 초기화 (주의!)
make docker-down
rm -rf data/mariadb/*
rm -rf data/redis/*
rm -rf data/kafka_local/*

# Redis 데이터 확인
docker exec -it commerce-redis redis-cli DBSIZE
```

## 트러블슈팅

### 포트 충돌

```bash
# 사용 중인 포트 확인
lsof -i :3306
lsof -i :6379
lsof -i :9092

# 프로세스 종료
kill -9 <PID>
```

### 컨테이너 재시작

```bash
# 특정 컨테이너 재시작
docker restart commerce-mariadb
docker restart commerce-redis

# 헬스체크 확인
make docker-health
```

### 로그 확인

```bash
# 실시간 로그
docker logs -f commerce-mariadb

# 최근 100줄
docker logs --tail 100 commerce-mariadb
```

## 개발 환경 시작 순서

```bash
cd infra

# 1. DB & Redis 시작
make docker-core

# 2. Kafka 시작 (필요시)
make docker-kafka

# 3. 모니터링 시작 (필요시)
make docker-monitoring

# 4. 서비스 실행
./gradlew :services:auth-service:bootRun
```
