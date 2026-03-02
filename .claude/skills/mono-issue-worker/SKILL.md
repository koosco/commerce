---
name: mono-issue-worker
description: GitHub 이슈들을 병렬로 구현하고 PR을 자동으로 생성합니다. 여러 이슈를 동시에 작업하고 싶을 때 사용합니다.
---

## 개요

GitHub 이슈 목록을 받아서 각 이슈마다 독립된 git worktree에서 subagent를 실행하고, 작업 완료 후 자동으로 PR을 생성합니다.

## 실행 절차

### Phase 1: 이슈 수집 및 분석

1. **이슈 목록 가져오기**
    - 사용자가 이슈 번호를 직접 지정한 경우: 해당 이슈들 사용
    - 사용자가 필터를 지정한 경우 (예: "priority:medium"): `gh issue list --label "priority:medium"` 으로 조회
    - 미지정 시: `gh issue list --state open --limit 20` 으로 목록을 보여주고 AskUserQuestion으로 선택 요청

2. **이슈 상세 조회**: 각 이슈에 대해 `gh issue view {number} --json title,body,labels` 실행

3. **의존성 분석**: 이슈 본문에서 "선행 작업", "depends on", "blockedBy" 등의 키워드를 찾아 의존성 그래프 구성
    - 의존성이 있는 이슈는 선행 이슈 완료 후 순차 실행
    - 독립적인 이슈는 모두 병렬 실행
    - 겹치는 이슈는 하나로 통합 (예: 같은 서비스의 Circuit Breaker)

4. **실행 계획 확인**: 사용자에게 실행 계획을 보여주고 승인 요청
   ```
   Batch 1 (병렬): #55, #27, #26, #21
   Batch 2 (#36 완료 후): #37
   ```

### Phase 2: 병렬 Subagent 실행

**CRITICAL**: 독립적인 이슈들은 반드시 하나의 메시지에서 여러 Task 도구를 동시에 호출해야 합니다.

각 subagent에 다음 설정 사용:

| 설정                | 값                 |
|-------------------|-------------------|
| subagent_type     | `general-purpose` |
| isolation         | `worktree`        |
| run_in_background | `true`            |

#### Subagent 프롬프트 템플릿

```
You are working on GitHub issue #{number}: "{title}"

## Project Context
This is a Kotlin/Spring Boot multi-module Gradle project (mono repo).
- Working directory: {working_directory}
- Services: `services/` directory
- Common modules: `common/` directory
- Each service follows Clean Architecture: api → application → domain ← infra

## IMPORTANT - Read project docs first:
- `.claude/CLAUDE.md` - 프로젝트 전체 가이드
- Kafka 관련 작업 시: `/mono-kafka` skill 참조

## Issue Details
{issue_body}

## Instructions
1. 먼저 관련 코드를 탐색하여 현재 구조를 파악하세요
2. 이슈에 명시된 구현 사항을 수행하세요
3. 기존 코드 컨벤션과 패턴을 따르세요
4. `./gradlew {affected_modules} spotlessApply` 실행
5. `./gradlew {affected_modules} compileKotlin` 으로 빌드 검증
6. 커밋 메시지: "{commit_prefix}: {short_description} (#{number})"

## Key Constraints
- Clean Architecture: application/domain 계층은 api/infra에 의존하면 안 됨
- Kafka 이벤트: CloudEvent 포맷 필수
- Consumer group ID: property 참조 필수 (하드코딩 금지)
- Port는 `IntegrationEventProducer`, Adapter는 `OutboxIntegrationEventProducer`
- 관련 없는 파일은 수정하지 마세요
```

### Phase 3: 완료 감지 및 PR 생성

각 subagent가 완료되면:

1. **완료 결과 확인**: agent 결과에서 worktree 경로와 브랜치명 추출

2. **브랜치 생성 및 cherry-pick**:
   ```bash
   # worktree 브랜치에서 커밋 해시 확인
   git log {worktree_branch} --not main --oneline

   # main 기반으로 이슈별 브랜치 생성
   git checkout -b {branch_name} main
   git cherry-pick {commit_hash}
   ```

3. **브랜치 네이밍 규칙**:

| 이슈 유형       | 브랜치 패턴                  | 예시                            |
|-------------|-------------------------|-------------------------------|
| Feature     | `feat/{short-name}`     | `feat/stock-event-consumer`   |
| Refactor    | `refactor/{short-name}` | `refactor/idempotency-header` |
| Improvement | `feat/{short-name}`     | `feat/circuit-breaker`        |
| Bug Fix     | `fix/{short-name}`      | `fix/redis-timeout`           |

4. **Push 및 PR 생성**:
   ```bash
   git push -u origin {branch_name}

   gh pr create --head {branch_name} --base main \
     --title "{pr_title}" \
     --body "$(cat <<'EOF'
   ## Summary
   {변경 사항 요약 - 3줄 이내}

   Closes #{issue_number}

   🤖 Generated with [Claude Code](https://claude.com/claude-code)
   EOF
   )"
   ```

5. **PR body의 `Closes #{number}`**: 머지 시 이슈 자동 close

### Phase 4: 의존성 있는 이슈 처리

선행 이슈의 PR이 머지된 후:

1. `git checkout main && git pull origin main`
2. 다음 배치의 subagent 실행 (Phase 2와 동일)
3. 완료 후 PR 생성 (Phase 3와 동일)

### Phase 5: 결과 보고

모든 작업 완료 후 결과 테이블 출력:

```
| 이슈 | PR | 상태 |
|------|-----|------|
| #55 Idempotency Key | PR #65 | 생성 완료 |
| #36 재고 연동 | PR #67 | 생성 완료 |
```

## 사용자 옵션

### 자동 머지 요청 시

사용자가 "머지까지 해줘" 또는 "main에 merge"라고 요청하면:

1. 의존성 순서대로 PR 머지: `gh pr merge {pr_number} --merge --delete-branch`
2. 머지 후 main pull: `git checkout main && git pull origin main`
3. 이슈 close 확인 (Closes 키워드로 자동 close 되지만 확인)

### 머지 없이 PR만 생성

기본 동작. PR 생성까지만 수행하고 결과 보고.

## 주의사항

1. **병렬 한계**: 동시 subagent는 최대 7개 권장 (리소스 제한)
2. **충돌 방지**: 같은 파일을 수정하는 이슈들은 의존성으로 분류하여 순차 실행
3. **common 모듈**: common 모듈 수정이 필요한 이슈는 먼저 처리
4. **빌드 검증**: 각 subagent가 자체적으로 compileKotlin 수행
5. **spotless**: 커밋 전 반드시 spotlessApply 실행
6. **worktree 정리**: 작업 완료 후 불필요한 worktree는 자동 정리됨

## 사용 예시

```
사용자: /mono-issue-worker 55 36 37 27
→ #55, #27은 병렬 실행
→ #36 병렬 실행
→ #37은 #36 완료 후 실행
→ 각각 PR 생성

사용자: /mono-issue-worker priority:medium
→ priority:medium 라벨 이슈 전체 조회
→ 의존성 분석 후 배치 실행
→ PR 생성

사용자: /mono-issue-worker 55 36 37 --merge
→ 작업 + PR 생성 + main 머지까지 자동
```
