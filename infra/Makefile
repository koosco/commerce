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

# Services (all 5 microservices)
SERVICES = user-service catalog-service inventory-service order-service payment-service

# -------------------------------
# Include sub-makefiles
# -------------------------------
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
	@echo "  make k8s-apply-all ENV=prod    # 운영 환경 배포"
	@echo ""

.PHONY: help
