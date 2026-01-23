# ===============================
# Kubernetes Namespace 관리
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

.PHONY: k8s-ns-create k8s-ns-delete k8s-ns-list k8s-ns-info k8s-ns-switch
