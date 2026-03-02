---
name: mono-api
description: API 문서 업데이트 가이드. 새로운 API 추가, 기존 API 수정/삭제 시 API.md를 함께 업데이트해야 할 때 사용합니다.
---

## API 문서 관리 규칙

새로운 API를 추가하거나 기존 API를 수정/삭제할 때, **반드시** 프로젝트 루트의 `API.md`를 함께 업데이트해야 합니다.

## 업데이트 체크리스트

### API 추가 시

1. **TOC 업데이트**: 해당 서비스 섹션의 테이블에 새 API 행 추가
    - Method, Endpoint (앵커 링크 포함), Description, Auth 컬럼
2. **상세 섹션 추가**: 해당 서비스 하위에 API 상세 블록 추가
    - HTTP Method + Path (H3 제목)
    - 설명 및 Auth 표시
    - Request Body (JSON 예시)
    - Query/Path Parameters (해당 시)
    - Response (주요 필드가 있는 경우)

### API 수정 시

1. TOC의 Description이 변경되었다면 업데이트
2. 상세 섹션의 Request/Response 구조가 변경되었다면 업데이트

### API 삭제 시

1. TOC에서 해당 행 제거
2. 상세 섹션에서 해당 블록 제거

## 앵커 링크 규칙

TOC의 링크는 다음 규칙을 따릅니다:

```
[/api/path](#method-apipathsegments)
```

- HTTP method를 소문자로 prefix
- `/`를 제거하고 단어를 `-`로 연결
- `{param}`에서 중괄호 제거

예시:

- `GET /api/products/{productId}` → `#get-apiproductsproductid`
- `POST /api/carts/me/items` → `#post-apicartsmeitems`

## API.md 위치

```
mono/API.md
```

## 참고

- 모든 응답은 `ApiResponse<T>`로 래핑됩니다 (Common Response Format 섹션 참조)
- Auth 컬럼 값: `-` (인증 불필요), `JWT`, `@AuthId`, `ADMIN`
