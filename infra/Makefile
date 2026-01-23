# ===============================
# Infra 관리용 Makefile
# ===============================

.DEFAULT_GOAL := help

# -------------------------------
# Help
# -------------------------------
help:
	@echo "$(GREEN)========================================$(NC)"
	@echo "$(GREEN)  Commerce Infrastructure Makefile$(NC)"
	@echo "$(GREEN)========================================$(NC)"
	@echo ""
	@echo "$(YELLOW)Kafka 관리:$(NC)"
	@echo "  make kafka-local               - local Kafka 컨테이너 시작 (docker 사용)"
	@echo "  make kafka-dev                 - dev Kafka 컨테이너 시작 (k3d cluster 사용)"
	@echo "  make kafka-down                - Kafka 컨테이너 중지"
	@echo "  make kafka-logs                - Kafka 로그 확인"
	@echo "  make kafka-ps                  - Kafka 컨테이너 상태"
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
# Kubernetes (k8s) 관리
# ===============================

# -------------------------------
# Namespace 생성
# -------------------------------
k8s-ns-create:
	kubectl apply -f $(NAMESPACE_FILE)

# -------------------------------
# Namespace 삭제
# 주의: 네임스페이스 내 모든 리소스가 삭제됩니다
# -------------------------------
k8s-ns-delete:
	@echo "⚠️  WARNING: This will delete the '$(NAMESPACE)' namespace and ALL its resources!"
	@read -p "Are you sure? [y/N] " -n 1 -r; \
	echo; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		kubectl delete namespace $(NAMESPACE); \
	else \
		echo "Cancelled."; \
	fi

# -------------------------------
# Namespace 목록 확인
# -------------------------------
k8s-ns-list:
	kubectl get namespaces

# -------------------------------
# Namespace 상세 정보
# -------------------------------
k8s-ns-info:
	kubectl describe namespace $(NAMESPACE)

# -------------------------------
# 현재 컨텍스트를 해당 네임스페이스로 설정
# -------------------------------
k8s-ns-switch:
	kubectl config set-context --current --namespace=$(NAMESPACE)

# -------------------------------
# Ingress 적용
# 예: make k8s-ingress-apply (기본 dev)
# 예: make k8s-ingress-apply ENV=prod
# -------------------------------
k8s-ingress-apply:
	@echo "$(YELLOW)Applying $(ENV) ingress...$(NC)"
	kubectl apply -f $(INGRESS_FILE) -n $(NAMESPACE)
	@echo "$(GREEN)✓ Ingress applied ($(ENV) environment)$(NC)"

# -------------------------------
# Ingress 삭제 (환경별)
# 예: make k8s-ingress-delete (기본 dev)
# 예: make k8s-ingress-delete ENV=prod
# -------------------------------
k8s-ingress-delete:
	@echo "$(YELLOW)Deleting $(ENV) ingress...$(NC)"
	kubectl delete -f $(INGRESS_FILE) -n $(NAMESPACE)
	@echo "$(GREEN)✓ Ingress deleted ($(ENV) environment)$(NC)"

# -------------------------------
# 모든 Ingress 삭제
# -------------------------------
k8s-ingress-delete-all:
	kubectl delete ingress --all -n $(NAMESPACE)

# -------------------------------
# Ingress 목록 확인
# -------------------------------
k8s-ingress-list:
	kubectl get ingress -n $(NAMESPACE)

# -------------------------------
# Ingress 상세 정보
# 예: make k8s-ingress-describe NAME=commerce-ingress
# -------------------------------
k8s-ingress-describe:
	@if [ -z "$(NAME)" ]; then \
		kubectl describe ingress -n $(NAMESPACE); \
	else \
		kubectl describe ingress $(NAME) -n $(NAMESPACE); \
	fi

# -------------------------------
# Ingress 상세 정보 (YAML)
# 예: make k8s-ingress-get NAME=commerce-ingress
# -------------------------------
k8s-ingress-get:
	@if [ -z "$(NAME)" ]; then \
		kubectl get ingress -n $(NAMESPACE) -o yaml; \
	else \
		kubectl get ingress $(NAME) -n $(NAMESPACE) -o yaml; \
	fi

# -------------------------------
# Kubernetes 전체 상태 확인
# -------------------------------
k8s-status:
	@if [ -z "$(resource)" ]; then \
		echo "=== Namespace Status ==="; \
		kubectl get namespace $(NAMESPACE) 2>/dev/null || echo "Namespace '$(NAMESPACE)' does not exist"; \
		echo ""; \
		echo "=== Ingress ==="; \
		kubectl get ingress -n $(NAMESPACE) 2>/dev/null || echo "No ingresses found in namespace '$(NAMESPACE)'"; \
		echo ""; \
		echo "=== Services ==="; \
		kubectl get svc -n $(NAMESPACE) 2>/dev/null || echo "No services found in namespace '$(NAMESPACE)'"; \
		echo ""; \
		echo "=== Pods ==="; \
		kubectl get pods -n $(NAMESPACE) 2>/dev/null || echo "No pods found in namespace '$(NAMESPACE)'"; \
	else \
		if [ "$(resource)" = "namespace" ]; then \
			echo "=== Namespace Status ==="; \
			kubectl get namespace $(NAMESPACE); \
		elif [ "$(resource)" = "ingress" ]; then \
			echo "=== Ingress ==="; \
			kubectl get ingress -n $(NAMESPACE); \
		elif [ "$(resource)" = "svc" ] || [ "$(resource)" = "services" ]; then \
			echo "=== Services ==="; \
			kubectl get svc -n $(NAMESPACE); \
		elif [ "$(resource)" = "pods" ] || [ "$(resource)" = "pod" ]; then \
			echo "=== Pods ==="; \
			kubectl get pods -n $(NAMESPACE); \
		else \
			echo "Unknown resource: $(resource). Available: namespace | ingress | svc | pods"; \
		fi \
	fi


# -------------------------------
# 전체 k8s 리소스 적용
# 예: make k8s-apply-all (기본 dev)
# 예: make k8s-apply-all ENV=prod
# -------------------------------
k8s-apply-all:
	@echo "$(YELLOW)Applying all k8s resources ($(ENV) environment)...$(NC)"
	kubectl apply -f $(NAMESPACE_FILE)
	kubectl apply -f $(K8S_DIR)/common/ -n $(NAMESPACE)
	kubectl apply -f $(K8S_DIR)/services/ -n $(NAMESPACE)
	kubectl apply -f $(INGRESS_FILE) -n $(NAMESPACE)
	@echo "$(GREEN)✓ All resources applied$(NC)"

# -------------------------------
# 서비스 manifest 적용 (common + services)
# -------------------------------
k8s-services-apply:
	@echo "$(YELLOW)Applying service manifests...$(NC)"
	kubectl apply -f $(K8S_DIR)/common/ -n $(NAMESPACE)
	kubectl apply -f $(K8S_DIR)/services/ -n $(NAMESPACE)
	@echo "$(GREEN)✓ Service manifests applied$(NC)"

# -------------------------------
# 서비스 manifest 삭제
# -------------------------------
k8s-services-delete:
	@echo "$(YELLOW)Deleting service manifests...$(NC)"
	kubectl delete -f $(K8S_DIR)/services/ -n $(NAMESPACE) --ignore-not-found
	kubectl delete -f $(K8S_DIR)/common/ -n $(NAMESPACE) --ignore-not-found
	@echo "$(GREEN)✓ Service manifests deleted$(NC)"

# ===============================
# Kubernetes Deployment 관리
# ===============================

# -------------------------------
# 모든 서비스 중지 (replicas=0)
# 참고: 외부 DB 사용으로 MariaDB deployment 제거
# -------------------------------
k8s-stop:
	@echo "$(YELLOW)Stopping all services (scaling to 0)...$(NC)"
	@echo ""
	@for deploy in auth-service user-service catalog-service inventory-service order-service payment-service; do \
		replicas=$$(kubectl get deployment/$$deploy -n $(NAMESPACE) -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "0"); \
		if [ "$$replicas" = "0" ]; then \
			echo "  $(YELLOW)⊘$(NC) $$deploy: already stopped (replicas=0)"; \
		else \
			echo "  $(GREEN)→$(NC) $$deploy: stopping (replicas=$$replicas → 0)"; \
			kubectl scale deployment/$$deploy -n $(NAMESPACE) --replicas=0 2>/dev/null || echo "  $(RED)✗$(NC) $$deploy: not found"; \
		fi \
	done
	@echo ""
	@echo "$(GREEN)✓ Stop operation complete$(NC)"

# -------------------------------
# 모든 서비스 시작
# 참고: 외부 DB 사용으로 MariaDB deployment 제거
# -------------------------------
k8s-start:
	@echo "$(YELLOW)Starting all services...$(NC)"
	@echo ""
	@for deploy in auth-service user-service catalog-service inventory-service order-service payment-service; do \
		target=2; \
		replicas=$$(kubectl get deployment/$$deploy -n $(NAMESPACE) -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "0"); \
		if [ "$$replicas" = "$$target" ]; then \
			echo "  $(YELLOW)⊘$(NC) $$deploy: already running (replicas=$$target)"; \
		else \
			echo "  $(GREEN)→$(NC) $$deploy: starting (replicas=$$replicas → $$target)"; \
			kubectl scale deployment/$$deploy -n $(NAMESPACE) --replicas=$$target 2>/dev/null || echo "  $(RED)✗$(NC) $$deploy: not found"; \
		fi \
	done
	@echo ""
	@echo "$(GREEN)✓ Start operation complete$(NC)"

# -------------------------------
# 모든 서비스 스케일 조정
# 예: make k8s-scale REPLICAS=3
# -------------------------------
k8s-scale:
	@if [ -z "$(REPLICAS)" ]; then \
		echo "$(RED)Error: REPLICAS not specified$(NC)"; \
		echo "Usage: make k8s-scale REPLICAS=3"; \
		exit 1; \
	fi
	@echo "$(YELLOW)Scaling all services to $(REPLICAS) replicas...$(NC)"
	@kubectl scale deployment/auth-service -n $(NAMESPACE) --replicas=$(REPLICAS)
	@kubectl scale deployment/user-service -n $(NAMESPACE) --replicas=$(REPLICAS)
	@kubectl scale deployment/catalog-service -n $(NAMESPACE) --replicas=$(REPLICAS)
	@kubectl scale deployment/inventory-service -n $(NAMESPACE) --replicas=$(REPLICAS)
	@kubectl scale deployment/order-service -n $(NAMESPACE) --replicas=$(REPLICAS)
	@kubectl scale deployment/payment-service -n $(NAMESPACE) --replicas=$(REPLICAS)
	@echo "$(GREEN)✓ All services scaled to $(REPLICAS) replicas$(NC)"

# -------------------------------
# 모든 서비스 재시작
# -------------------------------
k8s-restart:
	@echo "$(YELLOW)Restarting all services...$(NC)"
	@kubectl rollout restart deployment/auth-service -n $(NAMESPACE)
	@kubectl rollout restart deployment/user-service -n $(NAMESPACE)
	@kubectl rollout restart deployment/catalog-service -n $(NAMESPACE)
	@kubectl rollout restart deployment/inventory-service -n $(NAMESPACE)
	@kubectl rollout restart deployment/order-service -n $(NAMESPACE)
	@kubectl rollout restart deployment/payment-service -n $(NAMESPACE)
	@echo "$(GREEN)✓ All services restarted$(NC)"

# -------------------------------
# Deployment 상태 확인
# -------------------------------
k8s-deployments:
	@echo "$(YELLOW)=== Deployments Status ===$(NC)"
	@kubectl get deployments -n $(NAMESPACE)
	@echo ""
	@echo "$(YELLOW)=== Pods Status ===$(NC)"
	@kubectl get pods -n $(NAMESPACE)

# -------------------------------
# 특정 서비스 스케일 조정
# 예: make k8s-scale-service SERVICE=catalog-service REPLICAS=3
# -------------------------------
k8s-scale-service:
	@if [ -z "$(SERVICE)" ] || [ -z "$(REPLICAS)" ]; then \
		echo "$(RED)Error: SERVICE and REPLICAS are required$(NC)"; \
		echo "Usage: make k8s-scale-service SERVICE=catalog-service REPLICAS=3"; \
		exit 1; \
	fi
	@echo "$(YELLOW)Scaling $(SERVICE) to $(REPLICAS) replicas...$(NC)"
	@kubectl scale deployment/$(SERVICE) -n $(NAMESPACE) --replicas=$(REPLICAS)
	@echo "$(GREEN)✓ $(SERVICE) scaled to $(REPLICAS) replicas$(NC)"

# ===============================
# 로컬 개발 (k3s)
# ===============================

# -------------------------------
# Traefik IP 및 접근 정보 확인
# -------------------------------
k8s-traefik-ip:
	@echo "$(GREEN)========================================$(NC)"
	@echo "$(GREEN)  Traefik LoadBalancer Info$(NC)"
	@echo "$(GREEN)========================================$(NC)"
	@echo ""
	@kubectl get svc traefik -n kube-system
	@echo ""
	@echo "$(YELLOW)✓ Access URLs:$(NC)"
	@echo "  External IP: http://$$(kubectl get svc traefik -n kube-system -o jsonpath='{.status.loadBalancer.ingress[0].ip}')"
	@echo "  NodePort:    http://localhost:$$(kubectl get svc traefik -n kube-system -o jsonpath='{.spec.ports[0].nodePort}')"
	@echo ""
	@echo "$(YELLOW)💡 Tip: Use 'make k8s-port-forward' for localhost:80$(NC)"

# -------------------------------
# localhost 포트 포워딩 (개발용)
# 기본: 8080 포트 (권한 불필요)
# 예: make k8s-port-forward
# 예: make k8s-port-forward PORT=3000
# 예: make k8s-port-forward PORT=80 (sudo 필요)
# -------------------------------
PORT ?= 8080

k8s-port-forward:
	@echo "$(GREEN)========================================$(NC)"
	@echo "$(GREEN)  Port Forwarding: localhost:$(PORT)$(NC)"
	@echo "$(GREEN)========================================$(NC)"
	@echo ""
	@echo "$(YELLOW)✓ Services available at:$(NC)"
	@echo "  - http://localhost:$(PORT)/api/users"
	@echo "  - http://localhost:$(PORT)/api/auth"
	@echo "  - http://localhost:$(PORT)/api/catalog"
	@echo "  - http://localhost:$(PORT)/api/orders"
	@echo ""
	@if [ "$(PORT)" -lt "1024" ]; then \
		echo "$(RED)⚠ Port $(PORT) requires sudo (privileged port)$(NC)"; \
		echo "$(YELLOW)Run: sudo make k8s-port-forward PORT=$(PORT)$(NC)"; \
		echo "$(YELLOW)Or use: make k8s-port-forward PORT=8080$(NC)"; \
		echo ""; \
	fi
	@echo "$(YELLOW)Press Ctrl+C to stop$(NC)"
	@echo ""
	kubectl port-forward -n kube-system svc/traefik $(PORT):80

.PHONY: help \
	kafka-local kafka-dev kafka-down kafka-logs kafka-topics kafka-topic-create kafka-topic-delete kafka-ps \
	k8s-ns-create k8s-ns-delete k8s-ns-list k8s-ns-info k8s-ns-switch \
	k8s-ingress-apply k8s-ingress-delete k8s-ingress-delete-all \
	k8s-ingress-list k8s-ingress-describe k8s-ingress-get \
	k8s-status k8s-apply-all k8s-services-apply k8s-services-delete \
	k8s-stop k8s-start k8s-scale k8s-restart k8s-deployments k8s-scale-service \
	k8s-traefik-ip k8s-port-forward
