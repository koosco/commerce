# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with the infrastructure directory.

## Directory Purpose

Infrastructure as Code (IaC) for the commerce microservices platform. This directory centralizes all infrastructure provisioning for local development and Kubernetes deployment environments.

## Directory Structure

```
infra/
├── docker-compose.yaml       # Integrated Docker Compose (profile-based)
├── .env                      # Default environment variables
├── .env.local                # Local development settings
├── .env.dev                  # k3d development settings
├── .env.prod                 # Production settings (template)
├── Makefile                  # Central command interface
├── makefiles/                # Modular Makefile includes
│   ├── docker.mk             # Docker Compose commands
│   ├── kafka.mk              # Kafka topic management
│   ├── k8s-*.mk              # Kubernetes commands
├── config/                   # Configuration files
│   ├── redis/redis.conf      # Redis configuration
│   ├── prometheus/           # Prometheus scrape configs
│   └── grafana/              # Grafana provisioning & dashboards
├── data/                     # Persistent data (gitignored)
│   ├── mariadb/
│   ├── redis/
│   ├── kafka_local/
│   ├── kafka_dev/
│   ├── prometheus/
│   └── grafana/
├── k8s/                      # Kubernetes manifests
│   ├── namespace.yaml
│   ├── ingress-dev.yaml
│   ├── ingress.yaml
│   ├── common/               # ConfigMap, Secret
│   └── services/             # Service Deployments
├── docker/                   # Legacy (backup files)
├── kafka/                    # Legacy (backup files)
└── monitoring/               # Legacy (backup files)
```

## Docker Compose Profiles

The integrated `docker-compose.yaml` uses profiles for selective service startup:

| Profile | Services | Use Case |
|---------|----------|----------|
| `core` | MariaDB, Redis | Basic development |
| `kafka` | Kafka, Debezium, Kafka UI | Event-driven development |
| `monitoring` | Prometheus, Grafana, Node Exporter | Metrics collection |
| `full` | All services | Full integration testing |

### Quick Start Commands

```bash
# Core services only (DB + Redis)
make docker-core

# Core + Kafka
make docker-kafka

# Core + Monitoring
make docker-monitoring

# All services
make docker-full

# Environment-specific shortcuts
make docker-local   # Local development (full stack)
make docker-dev     # k3d development (core + kafka)

# Stop all services
make docker-down
```

### Manual Docker Compose Usage

```bash
# Profile-based startup
docker compose --profile core up -d
docker compose --profile core --profile kafka up -d
docker compose --profile full up -d

# Environment-specific
docker compose --env-file .env.local --profile full up -d
docker compose --env-file .env.dev --profile core --profile kafka up -d
```

## Service Ports

| Service | Port | Description |
|---------|------|-------------|
| MariaDB | 3306 | Database |
| Redis | 6379 | Cache |
| Kafka | 9092 | Message broker |
| Kafka Controller | 9093 | KRaft controller |
| Kafka JMX | 9999 | Monitoring |
| Debezium | 18083 | CDC connector REST API |
| Kafka UI | 18080 | Web interface |
| Prometheus | 9090 | Metrics |
| Grafana | 3000 | Dashboards (admin/admin123) |
| Node Exporter | 9100 | System metrics |

## Environment Configuration

### .env.local (Default)
- Kafka advertises on `host.docker.internal`
- Prometheus uses `prometheus.local.yml`
- Suitable for local Docker development

### .env.dev (k3d)
- Kafka advertises on `host.k3d.internal`
- Services inside k3d access Docker containers via host.k3d.internal
- Kafka data stored in `data/kafka_dev/`

### .env.prod (Template)
- Placeholder for production values
- External services (RDS, ElastiCache, MSK) preferred

## Makefile Commands

All operations are executed from the `infra/` directory.

### Docker Compose Commands

```bash
# Start services
make docker-core              # MariaDB + Redis
make docker-kafka             # Core + Kafka stack
make docker-monitoring        # Core + Prometheus/Grafana
make docker-full              # All services
make docker-local             # Full stack (local env)
make docker-dev               # Core + Kafka (k3d env)

# Stop services
make docker-down              # Stop all
make docker-down-v            # Stop all + remove volumes

# Status & Logs
make docker-ps                # Container status
make docker-status            # Detailed status with resources
make docker-logs              # All logs
make docker-logs SERVICE=db   # Specific service logs
make docker-health            # Health check all services

# Database access
make docker-db-shell          # MariaDB CLI (admin)
make docker-db-root           # MariaDB CLI (root)
make docker-redis-shell       # Redis CLI

# Kafka
make docker-kafka-topics      # List topics
make docker-kafka-describe TOPIC=name  # Describe topic
```

### Kafka Topic Management

```bash
make kafka-topics             # List all topics
make kafka-topic-create TOPIC=<name>   # Create topic
make kafka-topic-delete TOPIC=<name>   # Delete topic
make kafka-topics-describe TOPIC=<name> # Topic details
```

### Kubernetes Commands

```bash
# Namespace
make k8s-ns-create            # Create namespace
make k8s-ns-delete            # Delete namespace

# Resources
make k8s-apply-all ENV=dev    # Apply all (dev)
make k8s-apply-all ENV=prod   # Apply all (prod)
make k8s-status               # Check status

# Deployments
make k8s-start                # Start services
make k8s-stop                 # Stop services
make k8s-restart              # Rolling restart
make k8s-scale REPLICAS=3     # Scale all services

# Ingress
make k8s-ingress-apply ENV=dev
make k8s-ingress-list

# Local access
make k8s-port-forward PORT=8080
```

## Development Workflows

### Local Development

```bash
# 1. Start core infrastructure
make docker-core

# 2. (Optional) Add Kafka for event-driven features
make docker-kafka

# 3. (Optional) Add monitoring
make docker-monitoring

# 4. Run Spring Boot applications locally
# They connect to localhost:3306 (DB), localhost:6379 (Redis), localhost:9092 (Kafka)
```

### k3d Development

```bash
# 1. Start infrastructure (k3d accesses via host.k3d.internal)
make docker-dev

# 2. Create namespace and deploy services
make k8s-ns-create
make k8s-apply-all ENV=dev
make k8s-start

# 3. Access services
make k8s-port-forward PORT=8080
```

### Full Integration Testing

```bash
# Start everything
make docker-full

# Verify health
make docker-health

# View metrics
# Open http://localhost:3000 (Grafana)
```

## Kubernetes Details

### External Database Usage

**IMPORTANT**: k8s uses Docker Compose MariaDB (`host.k3d.internal:3306`).
No MariaDB is deployed inside k8s.

| Service | Database Schema |
|---------|-----------------|
| auth-service | commerce-auth |
| user-service | commerce-user |
| catalog-service | commerce-catalog |
| inventory-service | commerce-inventory |
| order-service | commerce-order |
| payment-service | commerce-payment |

### Service Configuration

- Deployments: 2 replicas (HA)
- `imagePullPolicy: Never` (k3d image import)
- Health probes: liveness (60s), readiness (30s)
- Resources: 512Mi-1Gi memory, 250m-1000m CPU

### Ingress Routing (Traefik)

| Path | Service |
|------|---------|
| `/api/auth` | auth-service |
| `/api/users` | user-service |
| `/api/orders` | order-service |
| `/api/catalog` | catalog-service |
| `/api/inventory` | inventory-service |
| `/api/payments` | payment-service |

## Load Testing Integration

Prometheus receives k6 metrics via remote write:

```bash
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
k6 run --out experimental-prometheus-rw scripts/<test>.js
```

View results in Grafana at http://localhost:3000 (admin/admin123).

## Troubleshooting

### Database Connection Failed
```bash
# Check if MariaDB is running
make docker-health
docker logs commerce-mariadb
```

### Kafka Not Ready
```bash
# Kafka needs time to initialize (60s startup)
make docker-logs SERVICE=kafka
```

### k3d Cannot Access Docker Services
```bash
# Ensure using .env.dev with host.k3d.internal
make docker-dev  # Uses correct advertised host
```

### Data Persistence Issues
```bash
# Check data directories
ls -la data/

# Clean all data (caution!)
make docker-data-clean
```
