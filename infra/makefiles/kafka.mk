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

# ===============================
# Debezium Connector 관리
# ===============================

# Debezium REST API endpoint
DEBEZIUM_URL ?= http://localhost:18083

# -------------------------------
# Connector 목록 확인
# -------------------------------
debezium-connectors:
	@curl -s $(DEBEZIUM_URL)/connectors | jq .

# -------------------------------
# Connector 상태 확인
# 예: make debezium-status CONNECTOR=outbox-connector
# -------------------------------
debezium-status:
	@curl -s $(DEBEZIUM_URL)/connectors/$(CONNECTOR)/status | jq .

# -------------------------------
# Outbox Connector 등록
# -------------------------------
debezium-outbox-register:
	@echo "Registering outbox connector..."
	@curl -s -X POST -H "Content-Type: application/json" \
		--data @config/debezium/outbox-connector.json \
		$(DEBEZIUM_URL)/connectors | jq .

# -------------------------------
# Outbox Connector 삭제
# -------------------------------
debezium-outbox-delete:
	@echo "Deleting outbox connector..."
	@curl -s -X DELETE $(DEBEZIUM_URL)/connectors/outbox-connector

# -------------------------------
# Outbox Connector 상태 확인
# -------------------------------
debezium-outbox-status:
	@curl -s $(DEBEZIUM_URL)/connectors/outbox-connector/status | jq .

# -------------------------------
# Outbox Connector 재시작
# -------------------------------
debezium-outbox-restart:
	@echo "Restarting outbox connector..."
	@curl -s -X POST $(DEBEZIUM_URL)/connectors/outbox-connector/restart

.PHONY: kafka-local kafka-dev kafka-down kafka-logs kafka-topics kafka-topic-create kafka-topic-delete kafka-ps \
        debezium-connectors debezium-status debezium-outbox-register debezium-outbox-delete debezium-outbox-status debezium-outbox-restart
