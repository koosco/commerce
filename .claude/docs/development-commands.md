# Development Commands

## Build Commands

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :services:auth-service:build
./gradlew :common:common-core:build

# Run tests (all modules)
./gradlew test

# Skip tests
./gradlew build -x test

# Format code
./gradlew spotlessApply

# Check formatting
./gradlew spotlessCheck
```

## Infrastructure (from `infra/` directory)

```bash
# Start local DB and Redis
cd infra/docker && docker-compose up -d

# Start Kafka (local)
make kafka-local

# Start monitoring (Prometheus + Grafana)
cd monitoring && docker-compose up -d
```

## Kubernetes (from `infra/` directory)

```bash
make k8s-ns-create          # Create namespace
make k8s-apply-all ENV=dev  # Apply all resources
make k8s-start              # Start all services
make k8s-stop               # Stop all services
make k8s-scale REPLICAS=3   # Scale services
make k8s-restart            # Rolling restart
```

## Docker Build (from service directory)

```bash
# Build JAR first
./gradlew :services:auth-service:build

# Build Docker image
cd services/auth-service
docker build -t auth-service:latest .
```

## Load Testing

Load testing uses **k6** (JavaScript-based) and is NOT part of the Gradle build system.

### Execution Rules (CRITICAL)

- Load tests must **NOT** be executed automatically
- Load tests must only be triggered **explicitly by the user**
- Do **NOT** add load tests to CI/CD pipelines
- Be mindful of resource usage in shared environments

### Test Structure (Three-Stage Approach)

```
Smoke Test → Baseline Test → Stress Test
```

| Stage | VUs | Duration | Purpose |
|-------|-----|----------|---------|
| Smoke | 1-2 | ~30s | Verify system is functional |
| Baseline | 20-50 | 5-10min | Establish performance baseline |
| Stress | 100+ | 15-30min | Find system limits |

### Commands (from `load-test/` directory)

```bash
# Install dependencies
npm install

# Run smoke test
k6 run scripts/inventory/decrease_concurrency/smoke.test.js

# Run baseline test
k6 run scripts/inventory/decrease_concurrency/baseline.test.js

# Run stress test
k6 run scripts/inventory/decrease_concurrency/stress.test.js
```

### Metrics & Visualization

- Metrics are exported to **Prometheus**
- Results are visualized in **Grafana** (`infra/monitoring/`)
- Key metrics: Response time (P50/P95/P99), Error rate, Throughput (RPS)

See `load-test/CLAUDE.md` for detailed guidance.

## Observability

- **Prometheus**: Metrics collection (port 9090)
- **Grafana**: Dashboards (port 3000, admin/admin123)
- **Actuator**: Each service exposes `/actuator/prometheus`

SSoT for monitoring: `infra/monitoring/`
