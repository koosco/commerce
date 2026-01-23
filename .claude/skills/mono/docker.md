# mono Docker Commands

이 skill은 로컬 개발 환경 Docker 명령어를 참조합니다.

## 사용 시점

- 로컬 인프라 (DB, Redis) 실행이 필요할 때
- Kafka 컨테이너 실행이 필요할 때
- 서비스 Docker 이미지 빌드가 필요할 때
- 모니터링 스택 실행이 필요할 때

## 디렉토리 구조

```
infra/
├── docker/                # DB, Redis
│   └── docker-compose.yaml
├── kafka/                 # Kafka, Debezium, Kafka UI
│   └── docker-compose.yaml
└── monitoring/            # Prometheus, Grafana
    └── docker-compose.yml
```

## Quick Reference

### 1. 데이터베이스 & Redis (infra/docker)

```bash
# 시작
cd infra/docker && docker-compose up -d

# 중지
cd infra/docker && docker-compose down

# 로그 확인
cd infra/docker && docker-compose logs -f

# 상태 확인
cd infra/docker && docker-compose ps
```

**서비스:**
- MariaDB: `localhost:3306` (admin/admin1234)
- Redis: `localhost:6379`

**데이터베이스:**
| Service | Database |
|---------|----------|
| auth-service | commerce-auth |
| user-service | commerce-user |
| catalog-service | commerce-catalog |
| inventory-service | commerce-inventory |
| order-service | commerce-order |
| payment-service | commerce-payment |

### 2. Kafka (infra/kafka) - Makefile 사용

```bash
# 로컬 Docker 환경 (host.docker.internal)
make kafka-local

# k3d 개발 환경 (host.k3d.internal)
make kafka-dev

# 중지
make kafka-down

# 로그 확인
make kafka-logs

# 상태 확인
make kafka-ps

# 토픽 목록
make kafka-topics

# 토픽 생성
make kafka-topic-create TOPIC=order-created

# 토픽 삭제
make kafka-topic-delete TOPIC=test-topic
```

**서비스:**
- Kafka: `localhost:9092`
- Kafka UI: `http://localhost:18080`
- Debezium Connect: `localhost:18083`

### 3. 모니터링 (infra/monitoring)

```bash
# 시작
cd infra/monitoring && docker-compose up -d

# 중지
cd infra/monitoring && docker-compose down

# 로그 확인
cd infra/monitoring && docker-compose logs -f
```

**서비스:**
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (admin/admin123)

### 4. 서비스 Docker 이미지 빌드

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

### 5. Docker Network

```bash
# commerce-net 네트워크 확인
docker network ls | grep commerce

# 네트워크 수동 생성 (필요시)
docker network create commerce-net
```

## docker-compose.yaml 예시 (infra/docker)

```yaml
services:
  mariadb:
    image: mariadb:11.8.5
    container_name: commerce-mariadb
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_USER: admin
      MYSQL_PASSWORD: admin1234
    ports:
      - "3306:3306"
    volumes:
      - ./mariadb_data:/var/lib/mysql
    networks:
      - commerce-net

  redis:
    image: redis:7.2
    container_name: commerce-redis
    ports:
      - "6379:6379"
    volumes:
      - ./redis/redis.conf:/usr/local/etc/redis/redis.conf
    command: redis-server /usr/local/etc/redis/redis.conf
    networks:
      - commerce-net

networks:
  commerce-net:
    driver: bridge
```

## 데이터베이스 접속

```bash
# MariaDB 접속
docker exec -it commerce-mariadb mysql -u admin -padmin1234

# 특정 데이터베이스 선택
docker exec -it commerce-mariadb mysql -u admin -padmin1234 commerce-order

# Redis CLI 접속
docker exec -it commerce-redis redis-cli
```

## 볼륨 관리

```bash
# 볼륨 목록
docker volume ls

# 데이터 초기화 (주의!)
cd infra/docker && docker-compose down -v
rm -rf mariadb_data/

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

# 전체 재시작
cd infra/docker && docker-compose restart
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
# 1. DB & Redis 시작
cd infra/docker && docker-compose up -d

# 2. Kafka 시작 (필요시)
make kafka-local

# 3. 모니터링 시작 (필요시)
cd infra/monitoring && docker-compose up -d

# 4. 서비스 실행
./gradlew :services:auth-service:bootRun
```
