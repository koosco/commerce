---
name: mono-infra-ops
description: 인프라 운영 절차 가이드. 새 서비스 추가, 환경변수 변경, 스케일링, 롤링 업데이트가 필요할 때 사용합니다.
---

## 동작 방식

인프라 운영 작업 시 필요한 절차를 안내하고 실행을 도와줍니다.

## 참조 문서

반드시 다음 문서를 읽고 작업하세요:

| 문서 | 내용 |
|------|------|
| `@infra/.claude/docs/operational-procedures.md` | 운영 절차 상세 (서비스 추가, 환경변수, 스케일링) |
| `@infra/.claude/docs/k8s-deployment-guide.md` | Deployment 패턴, Ingress 설정 |
| `@infra/.claude/docs/makefile-reference.md` | Makefile 명령어 레퍼런스 |
| `@infra/.claude/docs/troubleshooting-guide.md` | 문제 해결 시나리오 |

## 주요 작업

### 새 서비스 추가
1. `k8s/services/<name>.yaml` 생성 (기존 것 복사)
2. `k8s/ingress-dev.yaml`, `k8s/ingress.yaml` 경로 추가
3. `Makefile` SERVICES 변수 업데이트
4. `make k8s-services-apply && make k8s-ingress-apply ENV=dev`

### 환경 변수 변경
- 비민감: `k8s/common/configmap.yaml` 수정 → `kubectl apply` → `make k8s-restart`
- 민감: `k8s/common/secret.yaml` 수정 (base64) → `kubectl apply` → `make k8s-restart`

### 스케일링
- 전체: `make k8s-scale REPLICAS=N`
- 단일: `kubectl scale deployment/<name> --replicas=N -n commerce`
- 영구: 매니페스트 `spec.replicas` 수정 → `make k8s-services-apply`

### 롤링 업데이트
```bash
./gradlew :services:<name>:build
docker build -t <name>:latest services/<name>/
k3d image import <name>:latest -c <cluster>
kubectl rollout restart deployment/<name> -n commerce
```

## 주의사항

- ConfigMap/Secret 변경 후 반드시 `make k8s-restart`로 서비스 재시작
- base64 인코딩: `echo -n "value" | base64`
- base64 디코딩: `echo "encoded" | base64 -d`
