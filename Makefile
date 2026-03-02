# ===============================
# Commerce Mono Makefile
# ===============================

.DEFAULT_GOAL := help

# -------------------------------
# Variables
# -------------------------------
YELLOW := \033[1;33m
GREEN  := \033[0;32m
CYAN   := \033[0;36m
RED    := \033[0;31m
BOLD   := \033[1m
NC     := \033[0m

GRADLE   := ./gradlew
SERVICES := user-service catalog-service inventory-service order-service payment-service search-service

# ===============================
# Format
# ===============================

.PHONY: format
format: ## 전체 서비스 Spotless 포맷팅
	$(GRADLE) spotlessApply

.PHONY: format-check
format-check: ## 포맷팅 검사 (수정 없이 확인만)
	$(GRADLE) spotlessCheck

# ===============================
# Test
# ===============================

.PHONY: test
test: ## 전체 테스트 (make test) 또는 특정 서비스 (make test s=order-service)
ifdef s
	$(GRADLE) :services:$(s):test
else
	$(GRADLE) test
endif

.PHONY: test-coverage
test-coverage: ## 각 서비스 테스트 커버리지 확인
	@for svc in $(SERVICES); do \
		printf "$(CYAN)━━━ $$svc ━━━$(NC)\n"; \
		$(GRADLE) :services:$$svc:test :services:$$svc:jacocoTestReport --quiet 2>/dev/null; \
		report="services/$$svc/build/reports/jacoco/test/html/index.html"; \
		if [ -f "$$report" ]; then \
			echo "  Report: $$report"; \
		else \
			printf "  $(RED)No coverage report generated$(NC)\n"; \
		fi; \
		echo ""; \
	done
	@printf "$(GREEN)Coverage reports generated. Open HTML files in a browser to view.$(NC)\n"

.PHONY: test-coverage-verify
test-coverage-verify: ## 커버리지 기준 검증 (domain/application/api 80%)
	$(GRADLE) test jacocoTestCoverageVerification

# ===============================
# API
# ===============================

.PHONY: api
api: ## 전체 API 목록 (make api) 또는 특정 서비스 (make api s=order-service)
ifdef s
	@$(MAKE) --no-print-directory _api-print SVC=$(s)
else
	@$(MAKE) --no-print-directory _api-print SVC=user-service
	@$(MAKE) --no-print-directory _api-print SVC=catalog-service
	@$(MAKE) --no-print-directory _api-print SVC=inventory-service
	@$(MAKE) --no-print-directory _api-print SVC=order-service
	@$(MAKE) --no-print-directory _api-print SVC=payment-service
	@$(MAKE) --no-print-directory _api-print SVC=search-service
endif

# API.md에서 서비스별 테이블 추출
.PHONY: _api-print
_api-print:
	@header=$$(echo "$(SVC)" | sed 's/-service//' | awk '{print toupper(substr($$0,1,1)) substr($$0,2) " Service"}'); \
	printf "$(GREEN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(NC)\n"; \
	printf "$(BOLD) $$header$(NC)\n"; \
	printf "$(GREEN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(NC)\n"; \
	awk "/^### $$header/{found=1; next} found && /^### /{exit} found && /^\|/" API.md 2>/dev/null || \
		echo "  (not found in API.md)"; \
	echo ""

# ===============================
# Deploy - Local (프로세스 기반)
# ===============================

.PHONY: deploy
deploy: ## 로컬 실행 (make deploy) 전체 또는 (make deploy s=order-service)
ifdef s
	@printf "$(GREEN)Starting $(s) locally...$(NC)\n"
	$(GRADLE) :services:$(s):bootRun --args='--spring.profiles.active=local'
else
	@printf "$(GREEN)Starting all services locally...$(NC)\n"
	@for svc in $(SERVICES); do \
		printf "$(CYAN)Starting $$svc...$(NC)\n"; \
		$(GRADLE) :services:$$svc:bootRun --args='--spring.profiles.active=local' & \
	done; \
	echo ""; \
	printf "$(YELLOW)All services starting in background. Press Ctrl+C to stop all.$(NC)\n"; \
	wait
endif

.PHONY: deploy-stop
deploy-stop: ## 로컬에서 실행 중인 서비스 프로세스 종료
	@printf "$(YELLOW)Stopping local services...$(NC)\n"
	@for port in 8081 8082 8083 8084 8085 8087; do \
		pid=$$(lsof -ti :$$port 2>/dev/null); \
		if [ -n "$$pid" ]; then \
			printf "  Stopping PID $$pid (port $$port)\n"; \
			kill $$pid 2>/dev/null; \
		fi; \
	done
	@printf "$(GREEN)Done.$(NC)\n"

# ===============================
# Deploy - Production (k3s)
# ===============================

.PHONY: deploy-prod
deploy-prod: ## k3s 배포 (make deploy-prod) 전체 또는 (make deploy-prod s=order-service)
ifdef s
	@printf "$(GREEN)Deploying $(s) to k3s...$(NC)\n"
	$(GRADLE) :services:$(s):bootBuildImage
	cd infra && $(MAKE) k8s-deploy-one SVC=$(s)
else
	@printf "$(GREEN)Deploying all services to k3s...$(NC)\n"
	@for svc in $(SERVICES); do \
		printf "$(CYAN)Building $$svc image...$(NC)\n"; \
		$(GRADLE) :services:$$svc:bootBuildImage; \
	done
	cd infra && $(MAKE) k8s-apply-all ENV=prod
endif

.PHONY: deploy-prod-restart
deploy-prod-restart: ## k3s 재시작 (make deploy-prod-restart) 전체 또는 (make deploy-prod-restart s=order-service)
ifdef s
	kubectl rollout restart deployment/$(s) -n commerce
else
	cd infra && $(MAKE) k8s-restart
endif

# ===============================
# Build
# ===============================

.PHONY: build
build: ## 전체 빌드 (테스트 제외)
	$(GRADLE) build -x test

.PHONY: build-service
build-service: ## 특정 서비스 빌드 (make build-service s=order-service)
ifdef s
	$(GRADLE) :services:$(s):build -x test
else
	@printf "$(RED)Usage: make build-service s=<service-name>$(NC)\n"
endif

.PHONY: clean
clean: ## Gradle 빌드 클린
	$(GRADLE) clean

# ===============================
# Dependencies
# ===============================

.PHONY: deps
deps: ## 전체 의존성 트리
	$(GRADLE) dependencies

.PHONY: deps-service
deps-service: ## 특정 서비스 의존성 트리 (make deps-service s=order-service)
ifdef s
	$(GRADLE) :services:$(s):dependencies
else
	@printf "$(RED)Usage: make deps-service s=<service-name>$(NC)\n"
endif

# ===============================
# Docker (로컬 인프라)
# ===============================

.PHONY: infra-up
infra-up: ## 로컬 인프라 (DB, Redis, Kafka) 시작
	docker compose -f infra/docker/docker-compose.yaml up -d

.PHONY: infra-down
infra-down: ## 로컬 인프라 중지
	docker compose -f infra/docker/docker-compose.yaml down

.PHONY: infra-status
infra-status: ## 로컬 인프라 상태 확인
	docker compose -f infra/docker/docker-compose.yaml ps

# ===============================
# Monitoring
# ===============================

.PHONY: monitoring-up
monitoring-up: ## 모니터링 스택 (Prometheus, Grafana) 시작
	docker compose -f infra/monitoring/docker-compose.yaml up -d

.PHONY: monitoring-down
monitoring-down: ## 모니터링 스택 중지
	docker compose -f infra/monitoring/docker-compose.yaml down

# ===============================
# Utility
# ===============================

.PHONY: services
services: ## 서비스 목록과 포트 출력
	@printf "$(GREEN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(NC)\n"
	@printf "$(BOLD) Services$(NC)\n"
	@printf "$(GREEN)━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━$(NC)\n"
	@echo "  user-service        :8081"
	@echo "  search-service      :8082"
	@echo "  inventory-service   :8083"
	@echo "  catalog-service     :8084"
	@echo "  order-service       :8085"
	@echo "  payment-service     :8087"

.PHONY: health
health: ## 로컬 서비스 헬스체크
	@for entry in user-service:8081 search-service:8082 inventory-service:8083 catalog-service:8084 order-service:8085 payment-service:8087; do \
		svc=$${entry%%:*}; \
		port=$${entry##*:}; \
		status=$$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$$port/actuator/health 2>/dev/null); \
		if [ "$$status" = "200" ]; then \
			printf "$(GREEN)OK$(NC)  $$svc (:$$port)\n"; \
		else \
			printf "$(RED)--$(NC)  $$svc (:$$port)\n"; \
		fi; \
	done

.PHONY: logs
logs: ## k8s 로그 조회 (make logs s=order-service)
ifdef s
	kubectl logs -f -l app=$(s) -n commerce --tail=100
else
	@printf "$(RED)Usage: make logs s=<service-name>$(NC)\n"
endif

# ===============================
# Help
# ===============================

.PHONY: help
help: ## 도움말
	@printf "$(GREEN)========================================$(NC)\n"
	@printf "$(GREEN)  Commerce Mono Makefile$(NC)\n"
	@printf "$(GREEN)========================================$(NC)\n"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[0;36m%-24s\033[0m %s\n", $$1, $$2}'
	@echo ""
	@printf "$(YELLOW)서비스 지정:$(NC)  s=<service-name>\n"
	@printf "$(YELLOW)서비스 목록:$(NC)  user-service, catalog-service, inventory-service,\n"
	@echo "              order-service, payment-service, search-service"
	@echo ""
	@printf "$(YELLOW)예시:$(NC)\n"
	@echo "  make test                    # 전체 테스트"
	@echo "  make test s=order-service    # order-service 테스트"
	@echo "  make deploy s=user-service   # user-service 로컬 실행"
	@echo "  make api s=order-service     # order-service API 목록"
	@echo ""
