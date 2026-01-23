# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with the infrastructure directory.

## Directory Purpose

Infrastructure as Code (IaC) for the commerce microservices platform. This directory centralizes all infrastructure provisioning for local development and Kubernetes deployment environments.

## Directory Structure

```
infra/
├── Makefile              # Central command interface for all infrastructure operations
├── docker/               # Local development environment
│   ├── docker-compose.yaml
│   ├── mariadb_data/     # Persistent MariaDB data (gitignored)
│   └── redis/            # Redis configuration
├── kafka/                # Kafka provisioning (local and k3d)
│   ├── docker-compose.yaml
│   └── kafka_data_*/     # Kafka data directories (per profile)
├── k8s/                  # Kubernetes resource definitions
│   ├── namespace.yaml    # Commerce namespace
│   ├── ingress-dev.yaml  # Development Ingress (Traefik, HTTP)
│   ├── ingress.yaml      # Production Ingress (HTTPS, TLS)
│   ├── common/           # Shared ConfigMap and Secret
│   │   ├── configmap.yaml
│   │   └── secret.yaml
│   └── services/         # Service Deployment manifests
│       ├── auth-service.yaml
│       ├── user-service.yaml
│       ├── catalog-service.yaml
│       ├── inventory-service.yaml
│       ├── order-service.yaml
│       └── payment-service.yaml
└── monitoring/           # Observability stack (SSoT for monitoring)
    ├── docker-compose.yml          # Base configuration
    ├── docker-compose.override.yml # Local development overrides
    ├── docker-compose.prod.yml     # Production configuration
    ├── prometheus/                 # Prometheus configurations
    └── grafana/                    # Grafana dashboards and provisioning
```

## Subdirectory Details

### docker/

Local development database and cache infrastructure.

**Services**:
- **MariaDB** (11.8.5): Shared database container for all microservices
  - Port: 3306
  - Each service uses a separate database (commerce-auth, commerce-catalog, etc.)
  - Credentials: admin/admin1234 (root password: root)
- **Redis** (7.2): Caching and session storage
  - Port: 6379
  - Configuration: `redis/redis.conf`

**Network**: All services connect via `commerce-net` Docker network.

### kafka/

Apache Kafka provisioning using KRaft mode (no Zookeeper).

**Services**:
- **Kafka** (3.7.0): Message broker
  - Ports: 9092 (clients), 9093 (controller), 9999 (JMX)
  - KRaft mode with single broker/controller
- **Debezium Connect** (2.6): CDC connector for outbox pattern
  - Port: 18083
  - JSON converter configuration
- **Kafka UI**: Web interface for Kafka management
  - Port: 18080

**Profiles**:
- `local`: Uses `host.docker.internal` for local Docker access
- `dev`: Uses `host.k3d.internal` for k3d cluster access

### k8s/

Kubernetes resource definitions for the commerce namespace.

**IMPORTANT**: 외부 DB 사용 - Docker Compose MariaDB (`host.k3d.internal:3306`)를 사용합니다.
k8s 내부에 MariaDB를 배포하지 않습니다.

**Resources**:
- `namespace.yaml`: Creates the `commerce` namespace with production labels
- `ingress-dev.yaml`: Development Ingress configuration
  - Traefik middlewares for CORS and rate limiting
  - HTTP-only (no TLS)
  - Generous rate limits (1000 avg, 2000 burst)
- `ingress.yaml`: Production Ingress configuration
  - HTTPS with TLS
  - Stricter rate limiting

**common/** - 공통 설정:
- `configmap.yaml`: 공통 환경 변수 (DB_HOST, KAFKA, REDIS, JWT 설정)
- `secret.yaml`: 민감 정보 (DB credentials, JWT secret - base64 인코딩)

**services/** - 서비스별 Deployment + Service:
- 각 서비스는 Deployment(2 replicas)와 ClusterIP Service로 구성
- `imagePullPolicy: Never` - 로컬 빌드 이미지 사용 (k3d image import)
- Health probes: liveness (60s 초기 대기), readiness (30s 초기 대기)
- Resources: 512Mi~1Gi memory, 250m~1000m CPU

| 서비스 | DB 이름 | Kafka | Redis | 추가 환경변수 |
|--------|---------|-------|-------|---------------|
| auth-service | commerce-auth | X | X | - |
| user-service | commerce-user | X | X | AUTH_SERVICE_URL |
| catalog-service | commerce-catalog | O | X | - |
| inventory-service | commerce-inventory | O | O | - |
| order-service | commerce-order | O | X | SPRING_KAFKA_CONSUMER_GROUP_ID |
| payment-service | commerce-payment | O | X | SPRING_KAFKA_CONSUMER_GROUP_ID |

**Ingress Routing** (Traefik):
| Path | Service |
|------|---------|
| `/api/auth` | auth-service |
| `/api/users` | user-service |
| `/api/orders` | order-service |
| `/api/catalog` | catalog-service |
| `/api/inventory` | inventory-service |
| `/api/payments` | payment-service |
| `/api/notifications` | notification-service |

### monitoring/

Observability stack for metrics collection and visualization.

**IMPORTANT**: This is the Single Source of Truth (SSoT) for monitoring configuration. Do not duplicate monitoring specs in other directories.

**Services**:
- **Prometheus**: Metrics collection and storage
  - Port: 9090 (local)
  - Remote write receiver enabled for k6 metrics
  - 15-day retention
- **Grafana**: Visualization and dashboards
  - Port: 3000 (local)
  - Credentials: admin/admin123 (local)
  - Pre-provisioned dashboards for k6 and service metrics
- **Node Exporter**: System metrics (local only)
  - Port: 9100

**Configuration Files**:
- `prometheus.local.yml`: Local development scrape configuration
- `prometheus.prod.yml`: Production scrape configuration

## Makefile Commands

All operations are executed from the `infra/` directory. Reference the root `CLAUDE.md` for the full command list.

### Kafka Commands

```bash
make kafka-local          # Start Kafka for local Docker environment
make kafka-dev            # Start Kafka for k3d dev cluster
make kafka-down           # Stop Kafka containers
make kafka-logs           # View Kafka logs
make kafka-ps             # Check Kafka container status
make kafka-topics         # List all topics
make kafka-topic-create TOPIC=<name>   # Create a topic
make kafka-topic-delete TOPIC=<name>   # Delete a topic
```

### Kubernetes Commands

```bash
# Namespace
make k8s-ns-create        # Create commerce namespace
make k8s-ns-delete        # Delete namespace (with confirmation)
make k8s-ns-switch        # Switch kubectl context to commerce namespace

# Resources
make k8s-apply-all ENV=dev    # Apply all resources (namespace, common, services, ingress)
make k8s-apply-all ENV=prod   # Apply all resources (prod environment)
make k8s-status               # Check all resource status

# Service Manifests
make k8s-services-apply       # Apply common + service manifests
make k8s-services-delete      # Delete service manifests

# Deployments
make k8s-start            # Start all services (2 replicas each)
make k8s-stop             # Stop all services (scale to 0)
make k8s-restart          # Rolling restart all services
make k8s-scale REPLICAS=n # Scale application services
make k8s-deployments      # Check deployment and pod status

# Ingress
make k8s-ingress-apply ENV=dev    # Apply development Ingress
make k8s-ingress-apply ENV=prod   # Apply production Ingress
make k8s-ingress-list             # List Ingress resources
make k8s-ingress-describe         # Describe Ingress details

# Local Development (k3s)
make k8s-traefik-ip       # Get Traefik LoadBalancer IP
make k8s-port-forward PORT=8080   # Port forward to localhost
```

### Monitoring Commands

Run from the `monitoring/` subdirectory:

```bash
cd monitoring
docker-compose up -d      # Start Prometheus + Grafana (local)
docker-compose down       # Stop monitoring stack
```

## Environment Configuration

### Local Development

1. Start database infrastructure:
   ```bash
   cd docker && docker-compose up -d
   ```

2. Start Kafka (if needed):
   ```bash
   make kafka-local
   ```

3. Start monitoring (if needed):
   ```bash
   cd monitoring && docker-compose up -d
   ```

### Kubernetes (k3d) Development

**Prerequisites**:
- k3d cluster 실행 중
- Docker Compose 인프라 실행 중 (MariaDB, Redis)
- 서비스 이미지 빌드 및 import 완료

**Setup Steps**:

1. 외부 인프라 시작 (DB, Redis):
   ```bash
   cd docker && docker-compose up -d
   ```

2. Kafka for k3d 시작:
   ```bash
   make kafka-dev
   ```

3. K8s 리소스 배포:
   ```bash
   make k8s-ns-create
   make k8s-apply-all ENV=dev
   ```

4. 서비스 시작:
   ```bash
   make k8s-start
   ```

5. 서비스 접근:
   ```bash
   make k8s-port-forward PORT=8080
   # Services available at http://localhost:8080/api/*
   ```

**Troubleshooting**:
- DB 연결 실패 시: Docker Compose MariaDB가 실행 중인지 확인
- 이미지 pull 실패 시: `k3d image import <image>:latest -c <cluster>` 실행

## Service Deployment

Each microservice is deployed as:
- **Deployment**: Application pods (default 2 replicas for HA)
- **Service**: ClusterIP service for internal communication (port 80 → 8080)

**Database**: 외부 Docker Compose MariaDB 사용 (`host.k3d.internal:3306`)
- k8s 내부에 MariaDB를 배포하지 않음
- 각 서비스는 자체 schema 사용 (commerce-auth, commerce-user, etc.)

**Image Build & Deploy**:
```bash
# 1. JAR 빌드
./gradlew :services:auth-service:build

# 2. Docker 이미지 빌드
cd services/auth-service && docker build -t auth-service:latest .

# 3. k3d 클러스터로 이미지 import
k3d image import auth-service:latest -c <cluster-name>
```

Scaling is controlled via Makefile:
```bash
make k8s-scale REPLICAS=3              # Scale all app services
make k8s-scale-service SERVICE=auth-service REPLICAS=3  # Scale specific service
```

## Integration with Load Testing

The monitoring stack is designed to receive metrics from k6 load tests:

```bash
# From load-test directory
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw scripts/<test>.js
```

View results in Grafana at http://localhost:3000 (admin/admin123).
