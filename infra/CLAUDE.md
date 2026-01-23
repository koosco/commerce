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
│   └── ingress.yaml      # Production Ingress (HTTPS, TLS)
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

**Resources**:
- `namespace.yaml`: Creates the `commerce` namespace with production labels
- `ingress-dev.yaml`: Development Ingress configuration
  - Traefik middlewares for CORS and rate limiting
  - HTTP-only (no TLS)
  - Generous rate limits (1000 avg, 2000 burst)
- `ingress.yaml`: Production Ingress configuration
  - HTTPS with TLS
  - Stricter rate limiting

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
make k8s-apply-all ENV=dev    # Apply all resources (dev environment)
make k8s-apply-all ENV=prod   # Apply all resources (prod environment)
make k8s-status               # Check all resource status

# Deployments
make k8s-start            # Start all services (MariaDB: 1 replica, Apps: 2 replicas)
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

1. Ensure k3d cluster is running
2. Create namespace and apply resources:
   ```bash
   make k8s-ns-create
   make k8s-apply-all ENV=dev
   ```

3. Start Kafka for k3d:
   ```bash
   make kafka-dev
   ```

4. Start services:
   ```bash
   make k8s-start
   ```

5. Access services:
   ```bash
   make k8s-port-forward PORT=8080
   # Services available at http://localhost:8080/api/*
   ```

## Service Deployment

Each microservice is deployed as:
- **Deployment**: Application pods (default 2 replicas for HA)
- **Service**: ClusterIP service for internal communication
- **MariaDB**: Separate MariaDB deployment per service (1 replica)

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
