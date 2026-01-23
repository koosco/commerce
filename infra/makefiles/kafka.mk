# ===============================
# Kafka 관리
# ===============================

# -------------------------------
# Kafka 컨테이너 실행
# -------------------------------
kafka-local:
	docker compose -f $(KAFKA_COMPOSE) down
	KAFKA_PROFILE=local KAFKA_ADVERTISED_HOST=host.docker.internal docker compose -f $(KAFKA_COMPOSE) up -d

kafka-dev:
	docker compose -f $(KAFKA_COMPOSE) down
	KAFKA_PROFILE=dev KAFKA_ADVERTISED_HOST=host.k3d.internal docker compose -f $(KAFKA_COMPOSE) up -d

# -------------------------------
# Kafka 컨테이너 중지
# -------------------------------
kafka-down:
	docker compose -f $(KAFKA_COMPOSE) down

# -------------------------------
# Kafka 로그 확인
# -------------------------------
kafka-logs:
	docker compose -f $(KAFKA_COMPOSE) logs -f

# -------------------------------
# Kafka 토픽 목록 확인
# -------------------------------
kafka-topics:
	docker exec -it kafka-kraft \
		bash -c "/opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092"

# -------------------------------
# Kafka 토픽 생성
# 예: make kafka-topic-create TOPIC=test-topic
# -------------------------------
kafka-topic-create:
	docker exec -it kafka-kraft \
		bash -c "/opt/kafka/bin/kafka-topics.sh --create --topic $(TOPIC) --bootstrap-server localhost:9092"

# -------------------------------
# Kafka 토픽 삭제
# 예: make kafka-topic-delete TOPIC=test-topic
# -------------------------------
kafka-topic-delete:
	docker exec -it kafka-kraft \
		bash -c "/opt/kafka/bin/kafka-topics.sh --delete --topic $(TOPIC) --bootstrap-server localhost:9092"

# -------------------------------
# Kafka 컨테이너 상태
# -------------------------------
kafka-ps:
	docker compose -f $(KAFKA_COMPOSE) ps

.PHONY: kafka-local kafka-dev kafka-down kafka-logs kafka-topics kafka-topic-create kafka-topic-delete kafka-ps
