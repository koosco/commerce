# ===============================
# 로컬 개발 (k3s)
# ===============================

# Port default
PORT ?= 8080

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

.PHONY: k8s-traefik-ip k8s-port-forward
