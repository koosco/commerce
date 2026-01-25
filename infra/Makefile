# ===============================
# Infra 관리용 Makefile
# ===============================

.DEFAULT_GOAL := help

# -------------------------------
# Variables
# -------------------------------
# Colors
YELLOW = \033[1;33m
GREEN = \033[0;32m
RED = \033[0;31m
NC = \033[0m # No Color

# Kafka
KAFKA_COMPOSE = kafka/docker-compose.yaml
KAFKA_TOPICS_SCRIPT = scripts/kafka-topics-manager.sh
KAFKA_BOOTSTRAP_SERVERS ?= localhost:9092

# Kubernetes
K8S_DIR = k8s
NAMESPACE = commerce
NAMESPACE_FILE = $(K8S_DIR)/namespace.yaml

# Environment (dev or prod)
ENV ?= dev

# Ingress file selection based on environment
ifeq ($(ENV),prod)
    INGRESS_FILE = $(K8S_DIR)/ingress.yaml
else
    INGRESS_FILE = $(K8S_DIR)/ingress-dev.yaml
endif

# Services (all 6 microservices)
SERVICES = auth-service user-service catalog-service inventory-service order-service payment-service

# -------------------------------
# Include sub-makefiles
# -------------------------------
include makefiles/docker.mk
include makefiles/kafka.mk
include makefiles/k8s-namespace.mk
include makefiles/k8s-ingress.mk
include makefiles/k8s-deploy.mk
include makefiles/k8s-local.mk

# -------------------------------
# Help
# -------------------------------
help:
	@echo "$(GREEN)========================================$(NC)"
	@echo "$(GREEN)  Commerce Infrastructure Makefile$(NC)"
	@echo "$(GREEN)========================================$(NC)"
	@echo ""
	@echo "$(YELLOW)Docker Compose 관리:$(NC)"
	@echo "  make docker-core               - Core 서비스 시작 (MariaDB, Redis)"
	@echo "  make docker-kafka              - Core + Kafka 시작"
	@echo "  make docker-monitoring         - Core + Monitoring 시작"
	@echo "  make docker-full               - 전체 스택 시작"
	@echo "  make docker-local              - 로컬 개발 전체 스택"
	@echo "  make docker-dev                - k3d 개발 환경 (Core + Kafka)"
	@echo "  make docker-down               - 전체 서비스 중지"
	@echo "  make docker-ps                 - 컨테이너 상태 확인"
	@echo "  make docker-health             - 서비스 헬스 체크"
	@echo "  make docker-logs SERVICE=name  - 서비스 로그 확인"
	@echo ""
	@echo "$(YELLOW)Kafka 관리:$(NC)"
	@echo "  make kafka-local               - local Kafka 컨테이너 시작 (docker 사용)"
	@echo "  make kafka-dev                 - dev Kafka 컨테이너 시작 (k3d cluster 사용)"
	@echo "  make kafka-down                - Kafka 컨테이너 중지"
	@echo "  make kafka-logs                - Kafka 로그 확인"
	@echo "  make kafka-ps                  - Kafka 컨테이너 상태"
	@echo ""
	@echo "$(YELLOW)Debezium CDC 관리:$(NC)"
	@echo "  make debezium-connectors       - Connector 목록 확인"
	@echo "  make debezium-outbox-register  - Outbox Connector 등록"
	@echo "  make debezium-outbox-status    - Outbox Connector 상태"
	@echo "  make debezium-outbox-restart   - Outbox Connector 재시작"
	@echo "  make debezium-outbox-delete    - Outbox Connector 삭제"
	@echo ""
	@echo "$(YELLOW)Kubernetes 관리:$(NC)"
	@echo "  make k8s-ns-create             - Namespace 생성"
	@echo "  make k8s-ns-delete             - Namespace 삭제"
	@echo "  make k8s-status                - 전체 상태 확인"
	@echo "  make k8s-apply-all             - 모든 리소스 적용 (common, services, ingress)"
	@echo ""
	@echo "$(YELLOW)Kubernetes Services:$(NC)"
	@echo "  make k8s-services-apply        - 서비스 manifest 적용 (common + services)"
	@echo "  make k8s-services-delete       - 서비스 manifest 삭제"
	@echo ""
	@echo "$(YELLOW)Kubernetes Deployment:$(NC)"
	@echo "  make k8s-start                 - 모든 서비스 시작"
	@echo "  make k8s-stop                  - 모든 서비스 중지"
	@echo "  make k8s-restart               - 모든 서비스 재시작"
	@echo "  make k8s-scale                 - 스케일 조정 (REPLICAS=n)"
	@echo "  make k8s-deployments           - Deployment 상태"
	@echo ""
	@echo "$(YELLOW)Kubernetes Ingress:$(NC)"
	@echo "  make k8s-ingress-apply         - Ingress 적용 (ENV=dev|prod)"
	@echo "  make k8s-ingress-list          - Ingress 목록"
	@echo ""
	@echo "$(YELLOW)로컬 개발 (k3s):$(NC)"
	@echo "  make k8s-traefik-ip            - Traefik IP 확인"
	@echo "  make k8s-port-forward          - 포트 포워딩 (PORT=8080)"
	@echo ""
	@echo "$(YELLOW)예제:$(NC)"
	@echo "  make kafka-topics-setup                              # Kafka topics 전체 설정"
	@echo "  make kafka-topics-describe TOPIC=koosco.order.v1     # Topic 상세 정보"
	@echo "  make k8s-apply-all ENV=prod                          # 운영 환경 배포"
	@echo ""

.PHONY: help
