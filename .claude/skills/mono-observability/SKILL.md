---
name: mono-observability
description: Observability 가이드. Metrics, Logging, Tracing 설정 및 확인이 필요할 때 사용합니다.
---

## Metrics & Dashboards

- **Prometheus**: Metrics collection (port 9090)
- **Grafana**: Dashboards (port 3000, admin/admin123)
- **Actuator**: Each service exposes `/actuator/prometheus`
- SSoT for monitoring: `infra/monitoring/`

## Logging

### 공유 설정

`common-observability`의 `logback-spring.xml`이 모든 서비스에 적용됩니다.

### Profile별 동작

| Profile | 출력 | 레벨 | 용도 |
|---------|------|------|------|
| `local` | Plain text stdout | - | 로컬 개발 |
| `dev` | JSON stdout | DEBUG | k3d 개발 |
| `prod` | JSON stdout | INFO | Loki/Promtail 수집용 |

### JSON 포맷

LogstashEncoder 사용:
- `@timestamp`, `message`, `level`, `service`, `traceId`, `spanId`

### 주의사항

- common 모듈의 `application.properties`에 `spring.application.name` 설정 금지 (서비스 이름 덮어쓰기 방지)
