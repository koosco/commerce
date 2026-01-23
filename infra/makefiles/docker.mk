# ===============================
# Docker Compose Commands
# ===============================
# Profile-based infrastructure management
#
# Profiles:
#   - core: MariaDB, Redis
#   - kafka: Kafka, Debezium, Kafka UI
#   - monitoring: Prometheus, Grafana, Node Exporter
#   - full: All services
# ===============================

# Environment (default: local)
DOCKER_ENV ?= local
DOCKER_ENV_FILE := .env.$(DOCKER_ENV)
DOCKER_COMPOSE := docker compose --env-file $(DOCKER_ENV_FILE)

# Service filter (optional)
SERVICE ?=

# ===============================
# Profile-based Commands
# ===============================

## docker-core: Start core services (MariaDB, Redis)
docker-core:
	@echo "$(YELLOW)Starting core services (MariaDB, Redis)...$(NC)"
	$(DOCKER_COMPOSE) --profile core up -d
	@echo "$(GREEN)Core services started$(NC)"

## docker-kafka: Start core + Kafka services
docker-kafka:
	@echo "$(YELLOW)Starting core + Kafka services...$(NC)"
	$(DOCKER_COMPOSE) --profile core --profile kafka up -d
	@echo "$(GREEN)Core + Kafka services started$(NC)"

## docker-monitoring: Start core + monitoring services
docker-monitoring:
	@echo "$(YELLOW)Starting core + monitoring services...$(NC)"
	$(DOCKER_COMPOSE) --profile core --profile monitoring up -d
	@echo "$(GREEN)Core + monitoring services started$(NC)"

## docker-full: Start all services
docker-full:
	@echo "$(YELLOW)Starting all services...$(NC)"
	$(DOCKER_COMPOSE) --profile full up -d
	@echo "$(GREEN)All services started$(NC)"

# ===============================
# Environment-specific Shortcuts
# ===============================

## docker-local: Start full stack for local development
docker-local:
	@echo "$(YELLOW)Starting local development stack...$(NC)"
	docker compose --env-file .env.local --profile full up -d
	@echo "$(GREEN)Local stack started$(NC)"

## docker-dev: Start stack for k3d development (core + kafka)
docker-dev:
	@echo "$(YELLOW)Starting k3d development stack...$(NC)"
	docker compose --env-file .env.dev --profile core --profile kafka up -d
	@echo "$(GREEN)k3d development stack started$(NC)"

# ===============================
# Stop Commands
# ===============================

## docker-down: Stop all services
docker-down:
	@echo "$(YELLOW)Stopping all services...$(NC)"
	docker compose --profile full down
	@echo "$(GREEN)All services stopped$(NC)"

## docker-down-v: Stop all services and remove volumes
docker-down-v:
	@echo "$(RED)WARNING: This will remove all volumes!$(NC)"
	@read -p "Are you sure? [y/N] " confirm && [ "$$confirm" = "y" ] || exit 1
	docker compose --profile full down -v
	@echo "$(GREEN)All services and volumes removed$(NC)"

# ===============================
# Status Commands
# ===============================

## docker-ps: Show running containers
docker-ps:
	@docker compose ps -a

## docker-status: Show detailed status of all services
docker-status:
	@echo "$(YELLOW)Container Status:$(NC)"
	@docker compose ps -a
	@echo ""
	@echo "$(YELLOW)Resource Usage:$(NC)"
	@docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}" $$(docker compose ps -q 2>/dev/null) 2>/dev/null || echo "No running containers"

## docker-logs: View logs for all or specific service
docker-logs:
ifdef SERVICE
	docker compose logs -f $(SERVICE)
else
	docker compose logs -f
endif

## docker-logs-tail: View last 100 lines of logs
docker-logs-tail:
ifdef SERVICE
	docker compose logs --tail=100 $(SERVICE)
else
	docker compose logs --tail=100
endif

# ===============================
# Health Check Commands
# ===============================

## docker-health: Check health of all services
docker-health:
	@echo "$(YELLOW)Checking service health...$(NC)"
	@echo ""
	@echo "MariaDB:"
	@docker exec commerce-mariadb mysql -uadmin -padmin1234 -e "SELECT 1" 2>/dev/null && echo "  $(GREEN)OK$(NC)" || echo "  $(RED)FAILED$(NC)"
	@echo ""
	@echo "Redis:"
	@docker exec commerce-redis redis-cli PING 2>/dev/null && echo "  $(GREEN)OK$(NC)" || echo "  $(RED)FAILED$(NC)"
	@echo ""
	@echo "Kafka:"
	@docker exec kafka-kraft /opt/kafka/bin/kafka-broker-api-versions.sh --bootstrap-server localhost:9092 2>/dev/null | head -1 && echo "  $(GREEN)OK$(NC)" || echo "  $(RED)FAILED$(NC)"
	@echo ""
	@echo "Prometheus:"
	@curl -s http://localhost:9090/-/healthy 2>/dev/null && echo "  $(GREEN)OK$(NC)" || echo "  $(RED)FAILED$(NC)"
	@echo ""
	@echo "Grafana:"
	@curl -s http://localhost:3000/api/health 2>/dev/null | grep -q "ok" && echo "  $(GREEN)OK$(NC)" || echo "  $(RED)FAILED$(NC)"

# ===============================
# Database Commands
# ===============================

## docker-db-shell: Open MariaDB shell
docker-db-shell:
	@docker exec -it commerce-mariadb mysql -uadmin -padmin1234

## docker-db-root: Open MariaDB shell as root
docker-db-root:
	@docker exec -it commerce-mariadb mysql -uroot -proot

## docker-redis-shell: Open Redis CLI
docker-redis-shell:
	@docker exec -it commerce-redis redis-cli

# ===============================
# Kafka Commands (via integrated stack)
# ===============================

## docker-kafka-topics: List Kafka topics
docker-kafka-topics:
	@docker exec kafka-kraft /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

## docker-kafka-describe: Describe a Kafka topic (TOPIC=name)
docker-kafka-describe:
ifndef TOPIC
	@echo "$(RED)Usage: make docker-kafka-describe TOPIC=<topic-name>$(NC)"
else
	@docker exec kafka-kraft /opt/kafka/bin/kafka-topics.sh --describe --topic $(TOPIC) --bootstrap-server localhost:9092
endif

# ===============================
# Cleanup Commands
# ===============================

## docker-prune: Remove unused Docker resources
docker-prune:
	@echo "$(YELLOW)Cleaning up Docker resources...$(NC)"
	docker system prune -f
	@echo "$(GREEN)Cleanup complete$(NC)"

## docker-data-clean: Remove all data directories (DANGER!)
docker-data-clean:
	@echo "$(RED)WARNING: This will delete all persistent data!$(NC)"
	@read -p "Are you sure? [y/N] " confirm && [ "$$confirm" = "y" ] || exit 1
	rm -rf data/mariadb/* data/redis/* data/kafka_local/* data/kafka_dev/* data/prometheus/* data/grafana/*
	@echo "$(GREEN)Data directories cleaned$(NC)"

.PHONY: docker-core docker-kafka docker-monitoring docker-full \
        docker-local docker-dev docker-down docker-down-v \
        docker-ps docker-status docker-logs docker-logs-tail \
        docker-health docker-db-shell docker-db-root docker-redis-shell \
        docker-kafka-topics docker-kafka-describe \
        docker-prune docker-data-clean
