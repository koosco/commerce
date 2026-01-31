# CLAUDE.md

이 파일은 인프라 디렉토리 작업 시 Claude Code(claude.ai/code)를 위한 가이드를 제공합니다.

## 디렉토리 목적

커머스 플랫폼을 위한 Infrastructure as Code (IaC). 개발 및 운영 환경을 위한 Kubernetes 배포 설정을 관리합니다.

**중요**: 인프라 컴포넌트(MariaDB, Redis, Kafka, Prometheus, Grafana)는 **노드 레벨에서 전역 관리**되며, 이 디렉토리의 Docker Compose로 관리되지 않습니다.

## 아키텍처 개요

```
애플리케이션 레이어 (Kubernetes)         인프라 레이어 (노드 레벨)
├── 6개 서비스 (x2 복제본)    ──→    ├── MariaDB (3306)
└── host.k3d.internal 경유          ├── Redis (6379)
                                     ├── Kafka (9092)
                                     ├── Prometheus (9090)
                                     └── Grafana (3000)
```

**핵심 원칙**: 관심사 분리 (k8s=앱, 노드=인프라) | 외부 의존성 (`host.k3d.internal`) | 서비스당 2 복제본 | 환경 격리 (dev/prod)

## 디렉토리 구조

```
infra/
├── Makefile                  # 중앙 명령 인터페이스
├── .env*                     # 환경별 변수
├── k8s/                      # Kubernetes 매니페스트
│   ├── namespace.yaml
│   ├── ingress-dev.yaml / ingress.yaml
│   ├── common/               # configmap.yaml, secret.yaml
│   └── services/             # 서비스별 Deployment + Service
└── makefiles/                # 모듈화된 Makefile
```

## 설정 파일

| 파일 | 용도 |
|------|------|
| `.env` / `.env.local` / `.env.dev` / `.env.prod` | 환경별 NAMESPACE, ENV 변수 |
| `k8s/common/configmap.yaml` | 비민감 환경 변수 (DB, Kafka, Redis, JWT, 로그 레벨) |
| `k8s/common/secret.yaml` | 민감 데이터 (DB 자격 증명, JWT Secret) — base64 인코딩 |

상세: `@infra/.claude/docs/k8s-deployment-guide.md` (ConfigMap/Secret 변수 목록 포함)

## K8s Probe 전략

모든 서비스에 3종 프로브 적용:

| Probe | Path | 역할 |
|-------|------|------|
| startupProbe | `/actuator/health/liveness` | Spring Boot 기동 완료 대기 (30s initial + 20 retries) |
| livenessProbe | `/actuator/health/liveness` | 기동 후 alive 확인 (30s interval) |
| readinessProbe | `/actuator/health/readiness` | 트래픽 수신 가능 여부 (10s interval) |

**주의**: livenessProbe에 `initialDelaySeconds`를 넣지 않음 — startupProbe가 기동 기간을 커버

## Dockerfile 컨벤션

- **Base image**: `eclipse-temurin:21-jre-alpine`
- **Non-root user**: `spring:spring` (보안)
- **COPY --chown**: 별도 `RUN chown` 대신 COPY 단계에서 소유권 설정
- **exec ENTRYPOINT**: `exec java`로 PID 1 시그널 핸들링
- **HEALTHCHECK 미사용**: k8s probe가 담당
- **.dockerignore**: `.git`, `.gradle`, `**/src` 등 빌드 컨텍스트 최소화

## 상세 문서

| 문서 | 내용 |
|------|------|
| `@infra/.claude/docs/k8s-deployment-guide.md` | Deployment 패턴, Ingress 설정, ConfigMap/Secret |
| `@infra/.claude/docs/operational-procedures.md` | 서비스 추가, 환경변수 변경, 스케일링, 롤링 업데이트 |
| `@infra/.claude/docs/troubleshooting-guide.md` | 6가지 주요 문제 해결 시나리오 |
| `@infra/.claude/docs/makefile-reference.md` | Makefile 명령어 레퍼런스 |
| `@infra/.claude/docs/best-practices.md` | 리소스, 설정, 배포, 보안 모범 사례 |
