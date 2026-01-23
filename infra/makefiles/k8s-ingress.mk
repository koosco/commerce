# ===============================
# Kubernetes Ingress 관리
# ===============================

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

.PHONY: k8s-ingress-apply k8s-ingress-delete k8s-ingress-delete-all k8s-ingress-list k8s-ingress-describe k8s-ingress-get
