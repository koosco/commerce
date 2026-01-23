# ===============================
# Kubernetes Deployment 관리
# ===============================

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

# -------------------------------
# 모든 서비스 중지 (replicas=0)
# 참고: 외부 DB 사용으로 MariaDB deployment 제거
# -------------------------------
k8s-stop:
	@echo "$(YELLOW)Stopping all services (scaling to 0)...$(NC)"
	@echo ""
	@for deploy in $(SERVICES); do \
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
	@for deploy in $(SERVICES); do \
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
	@for deploy in $(SERVICES); do \
		kubectl scale deployment/$$deploy -n $(NAMESPACE) --replicas=$(REPLICAS); \
	done
	@echo "$(GREEN)✓ All services scaled to $(REPLICAS) replicas$(NC)"

# -------------------------------
# 모든 서비스 재시작
# -------------------------------
k8s-restart:
	@echo "$(YELLOW)Restarting all services...$(NC)"
	@for deploy in $(SERVICES); do \
		kubectl rollout restart deployment/$$deploy -n $(NAMESPACE); \
	done
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

.PHONY: k8s-status k8s-apply-all k8s-services-apply k8s-services-delete \
	k8s-stop k8s-start k8s-scale k8s-restart k8s-deployments k8s-scale-service
