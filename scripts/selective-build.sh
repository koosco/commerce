#!/usr/bin/env bash
#
# selective-build.sh - Git diff 기반 변경 감지 선택적 빌드 스크립트
#
# common 모듈 변경 시 의존하는 모든 서비스를 빌드하고,
# 서비스 모듈 변경 시 해당 서비스만 빌드합니다.
#
# Usage:
#   ./scripts/selective-build.sh [base_ref]
#
# Arguments:
#   base_ref  - 비교 대상 Git ref (기본값: origin/main)
#
# Examples:
#   ./scripts/selective-build.sh                  # origin/main 대비 변경 감지
#   ./scripts/selective-build.sh HEAD~1           # 직전 커밋 대비 변경 감지
#   ./scripts/selective-build.sh origin/develop   # develop 브랜치 대비 변경 감지

set -euo pipefail

BASE_REF="${1:-origin/main}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$ROOT_DIR"

# 변경된 파일 목록 조회
CHANGED_FILES=$(git diff --name-only "$BASE_REF"...HEAD 2>/dev/null || git diff --name-only "$BASE_REF" HEAD)

if [ -z "$CHANGED_FILES" ]; then
    echo "[selective-build] No changes detected against $BASE_REF. Nothing to build."
    exit 0
fi

echo "[selective-build] Changed files against $BASE_REF:"
echo "$CHANGED_FILES" | sed 's/^/  /'
echo ""

# 빌드 대상 모듈 수집
BUILD_TARGETS=()

# common 모듈 변경 감지 - 변경 시 전체 빌드
COMMON_CHANGED=false
if echo "$CHANGED_FILES" | grep -q "^common/"; then
    COMMON_CHANGED=true
fi

# 루트 빌드 파일 변경 감지 - 변경 시 전체 빌드
ROOT_BUILD_CHANGED=false
if echo "$CHANGED_FILES" | grep -qE "^(build\.gradle\.kts|settings\.gradle\.kts|gradle\.properties|gradle/)"; then
    ROOT_BUILD_CHANGED=true
fi

if [ "$COMMON_CHANGED" = true ] || [ "$ROOT_BUILD_CHANGED" = true ]; then
    if [ "$COMMON_CHANGED" = true ]; then
        echo "[selective-build] Common module changed. Building all services."
    fi
    if [ "$ROOT_BUILD_CHANGED" = true ]; then
        echo "[selective-build] Root build configuration changed. Building all modules."
    fi
    ./gradlew build
    exit $?
fi

# 개별 서비스 변경 감지
SERVICES=("user-service" "catalog-service" "inventory-service" "order-service" "payment-service")

for SERVICE in "${SERVICES[@]}"; do
    if echo "$CHANGED_FILES" | grep -q "^services/$SERVICE/"; then
        BUILD_TARGETS+=(":services:$SERVICE:build")
    fi
done

if [ ${#BUILD_TARGETS[@]} -eq 0 ]; then
    echo "[selective-build] No buildable module changes detected. Skipping build."
    exit 0
fi

echo "[selective-build] Building targets: ${BUILD_TARGETS[*]}"
./gradlew "${BUILD_TARGETS[@]}"
