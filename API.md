# API Reference

## Table of Contents

### User Service (:8081)

| #  | Method | Endpoint                                                                    | Description     | Auth    |
|----|--------|-----------------------------------------------------------------------------|-----------------|---------|
| 1  | POST   | [/api/auth/login](#post-apiauthlogin)                                       | 로그인             | -       |
| 2  | POST   | [/api/auth/refresh](#post-apiauthrefresh)                                   | 토큰 갱신           | -       |
| 3  | POST   | [/api/auth/logout](#post-apiauthlogout)                                     | 로그아웃            | @AuthId |
| 4  | POST   | [/api/users](#post-apiusers)                                                | 회원가입            | -       |
| 5  | GET    | [/api/users/{userId}](#get-apiusersuserid)                                  | 사용자 조회          | -       |
| 6  | PATCH  | [/api/users/me](#patch-apiusersme)                                          | 본인 정보 수정        | @AuthId |
| 7  | DELETE | [/api/users/me](#delete-apiusersme)                                         | 본인 탈퇴           | @AuthId |
| 8  | PATCH  | [/api/users/{userId}](#patch-apiusersuserid)                                | 사용자 정보 수정 (관리자) | ADMIN   |
| 9  | DELETE | [/api/users/{userId}](#delete-apiusersuserid)                               | 사용자 삭제 (관리자)    | ADMIN   |
| 10 | GET    | [/api/users/me/addresses](#get-apiusersmeaddresses)                         | 배송지 목록 조회       | @AuthId |
| 11 | POST   | [/api/users/me/addresses](#post-apiusersmeaddresses)                        | 배송지 등록          | @AuthId |
| 12 | DELETE | [/api/users/me/addresses/{addressId}](#delete-apiusersmeaddressesaddressid) | 배송지 삭제          | @AuthId |

### Catalog Service (:8084)

| #  | Method | Endpoint                                                              | Description | Auth    |
|----|--------|-----------------------------------------------------------------------|-------------|---------|
| 1  | GET    | [/api/products](#get-apiproducts)                                     | 상품 목록 조회    | -       |
| 2  | GET    | [/api/products/{productId}](#get-apiproductsproductid)                | 상품 상세 조회    | -       |
| 3  | GET    | [/api/products/{productId}/skus](#get-apiproductsproductidskus)       | SKU 조회      | -       |
| 4  | POST   | [/api/products](#post-apiproducts)                                    | 상품 등록       | JWT     |
| 5  | PUT    | [/api/products/{productId}](#put-apiproductsproductid)                | 상품 수정       | JWT     |
| 6  | PATCH  | [/api/products/{productId}/status](#patch-apiproductsproductidstatus) | 상품 상태 변경    | JWT     |
| 7  | DELETE | [/api/products/{productId}](#delete-apiproductsproductid)             | 상품 삭제       | JWT     |
| 8  | GET    | [/api/categories](#get-apicategories)                                 | 카테고리 목록 조회  | -       |
| 9  | GET    | [/api/categories/{categoryId}](#get-apicategoriescategoryid)          | 카테고리 단건 조회  | -       |
| 10 | GET    | [/api/categories/tree](#get-apicategoriestree)                        | 카테고리 트리 조회  | -       |
| 11 | POST   | [/api/categories](#post-apicategories)                                | 카테고리 생성     | JWT     |
| 12 | POST   | [/api/categories/tree](#post-apicategoriestree)                       | 카테고리 트리 생성  | JWT     |
| 13 | GET    | [/api/brands](#get-apibrands)                                         | 브랜드 목록 조회   | -       |
| 14 | GET    | [/api/brands/{brandId}](#get-apibrandsbrandid)                        | 브랜드 상세 조회   | -       |
| 15 | POST   | [/api/brands](#post-apibrands)                                        | 브랜드 등록      | JWT     |
| 16 | PUT    | [/api/brands/{brandId}](#put-apibrandsbrandid)                        | 브랜드 수정      | JWT     |
| 17 | DELETE | [/api/brands/{brandId}](#delete-apibrandsbrandid)                     | 브랜드 삭제      | JWT     |
| 18 | POST   | [/api/reviews](#post-apireviews)                                      | 리뷰 작성       | @AuthId |
| 19 | GET    | [/api/products/{productId}/reviews](#get-apiproductsproductidreviews) | 상품별 리뷰 조회   | -       |
| 20 | PUT    | [/api/reviews/{reviewId}](#put-apireviewsreviewid)                    | 리뷰 수정       | @AuthId |
| 21 | DELETE | [/api/reviews/{reviewId}](#delete-apireviewsreviewid)                 | 리뷰 삭제       | @AuthId |
| 22 | POST   | [/api/reviews/{reviewId}/like](#post-apireviewsreviewidlike)          | 리뷰 좋아요 토글   | @AuthId |
| 23 | POST   | [/api/snaps](#post-apisnaps)                                          | 스냅 작성       | @AuthId |
| 24 | GET    | [/api/snaps](#get-apisnaps)                                           | 스냅 피드 조회    | -       |
| 25 | PUT    | [/api/snaps/{snapId}](#put-apisnapssnapid)                            | 스냅 수정       | @AuthId |
| 26 | DELETE | [/api/snaps/{snapId}](#delete-apisnapssnapid)                         | 스냅 삭제       | @AuthId |
| 27 | POST   | [/api/snaps/{snapId}/like](#post-apisnapssnapidlike)                  | 스냅 좋아요 토글   | @AuthId |

### Inventory Service (:8083)

| # | Method | Endpoint                                                  | Description | Auth  |
|---|--------|-----------------------------------------------------------|-------------|-------|
| 1 | GET    | [/api/inventories/{skuId}](#get-apiinventoriesskuid)      | 재고 조회       | -     |
| 2 | POST   | [/api/inventories/bulk](#post-apiinventoriesbulk)         | 대량 재고 조회    | -     |
| 3 | POST   | [/api/inventories/increase](#post-apiinventoriesincrease) | 대량 재고 추가    | -     |
| 4 | POST   | [/api/inventories/decrease](#post-apiinventoriesdecrease) | 대량 재고 감소    | -     |
| 5 | GET    | [/api/inventories/logs](#get-apiinventorieslogs)          | 재고 변경 로그 조회 | ADMIN |

### Order Service (:8085)

| # | Method | Endpoint                                                              | Description    | Auth    |
|---|--------|-----------------------------------------------------------------------|----------------|---------|
| 1 | POST   | [/api/orders](#post-apiorders)                                        | 주문 생성          | JWT     |
| 2 | GET    | [/api/orders](#get-apiorders)                                         | 주문 목록 조회       | @AuthId |
| 3 | GET    | [/api/orders/{orderId}](#get-apiordersorderid)                        | 주문 상세 조회       | -       |
| 4 | POST   | [/api/orders/{orderId}/cancel](#post-apiordersorderidcancel)          | 주문 취소          | @AuthId |
| 5 | GET    | [/api/carts/me](#get-apicartsme)                                      | 장바구니 조회        | @AuthId |
| 6 | POST   | [/api/carts/me/items](#post-apicartsmeitems)                          | 장바구니 아이템 추가    | @AuthId |
| 7 | PATCH  | [/api/carts/me/items/{cartItemId}](#patch-apicartsmeitemscartitemid)  | 장바구니 아이템 수량 수정 | @AuthId |
| 8 | DELETE | [/api/carts/me/items/{cartItemId}](#delete-apicartsmeitemscartitemid) | 장바구니 아이템 제거    | @AuthId |
| 9 | DELETE | [/api/carts/me/items](#delete-apicartsmeitems)                        | 장바구니 전체 삭제     | @AuthId |

### Payment Service (:8087)

| # | Method | Endpoint                                          | Description | Auth |
|---|--------|---------------------------------------------------|-------------|------|
| 1 | POST   | [/api/payments/confirm](#post-apipaymentsconfirm) | 결제 승인       | -    |

---

## User Service

### POST /api/auth/login

로그인

**Request Body**

```json
{
  "email": "string",
  "password": "string"
}
```

**Response**

```json
{
  "accessToken": "string"
}
```

> Refresh Token은 `Set-Cookie` 헤더로 전달됩니다.

---

### POST /api/auth/refresh

토큰 갱신

Cookie에 포함된 Refresh Token으로 Access Token을 재발급합니다.

**Response**

```json
{
  "accessToken": "string"
}
```

---

### POST /api/auth/logout

로그아웃 | **Auth: @AuthId**

---

### POST /api/users

회원가입

**Request Body**

```json
{
  "email": "string",
  "password": "string",
  "name": "string",
  "phone": "string?",
  "idempotencyKey": "string?"
}
```

---

### GET /api/users/{userId}

사용자 조회

**Path Parameters**

| Name   | Type | Description |
|--------|------|-------------|
| userId | Long | 사용자 ID      |

---

### PATCH /api/users/me

본인 정보 수정 | **Auth: @AuthId**

**Request Body**

```json
{
  "name": "string?",
  "phone": "string?"
}
```

---

### DELETE /api/users/me

본인 탈퇴 | **Auth: @AuthId**

---

### PATCH /api/users/{userId}

사용자 정보 수정 (관리자) | **Auth: ADMIN**

**Request Body**

```json
{
  "name": "string?",
  "phone": "string?"
}
```

---

### DELETE /api/users/{userId}

사용자 삭제 (관리자) | **Auth: ADMIN**

---

### GET /api/users/me/addresses

배송지 목록 조회 | **Auth: @AuthId**

---

### POST /api/users/me/addresses

배송지 등록 | **Auth: @AuthId**

**Request Body**

```json
{
  "label": "string",
  "recipient": "string",
  "phone": "string",
  "zipCode": "string",
  "address": "string",
  "addressDetail": "string",
  "isDefault": "boolean? (default: false)",
  "idempotencyKey": "string?"
}
```

---

### DELETE /api/users/me/addresses/{addressId}

배송지 삭제 | **Auth: @AuthId**

---

## Catalog Service

### GET /api/products

상품 목록 조회

**Query Parameters**

| Name       | Type    | Description                                     |
|------------|---------|-------------------------------------------------|
| categoryId | Long?   | 카테고리 필터                                         |
| keyword    | String? | 검색 키워드                                          |
| brandId    | Long?   | 브랜드 필터                                          |
| minPrice   | Long?   | 최소 가격                                           |
| maxPrice   | Long?   | 최대 가격                                           |
| sort       | String  | LATEST, PRICE_ASC, PRICE_DESC (default: LATEST) |
| page       | Int     | 페이지 번호 (default: 0)                             |
| size       | Int     | 페이지 크기 (default: 20)                            |

---

### GET /api/products/{productId}

상품 상세 조회

---

### GET /api/products/{productId}/skus

옵션 조합으로 SKU 조회

**Query Parameters**: 동적 옵션 파라미터 (e.g., `?Volume=100ml&Package=Single`)

---

### POST /api/products

상품 등록 | **Auth: JWT**

**Request Body**

```json
{
  "name": "string",
  "description": "string?",
  "price": "long",
  "status": "ProductStatus? (default: DRAFT)",
  "categoryId": "long?",
  "thumbnailImageUrl": "string?",
  "brandId": "long?",
  "optionGroups": [
    {
      "name": "string",
      "ordering": "int",
      "options": [
        {
          "name": "string",
          "additionalPrice": "long",
          "ordering": "int"
        }
      ]
    }
  ],
  "idempotencyKey": "string?"
}
```

---

### PUT /api/products/{productId}

상품 수정 | **Auth: JWT**

**Request Body**

```json
{
  "name": "string?",
  "description": "string?",
  "price": "long?",
  "categoryId": "long?",
  "thumbnailImageUrl": "string?",
  "brandId": "long?"
}
```

---

### PATCH /api/products/{productId}/status

상품 상태 변경 | **Auth: JWT**

**Request Body**

```json
{
  "status": "ProductStatus (DRAFT | ACTIVE | INACTIVE | DISCONTINUED)"
}
```

---

### DELETE /api/products/{productId}

상품 삭제 | **Auth: JWT**

---

### GET /api/categories

카테고리 목록 조회

**Query Parameters**

| Name     | Type  | Description              |
|----------|-------|--------------------------|
| parentId | Long? | 부모 카테고리 ID (없으면 루트 카테고리) |

---

### GET /api/categories/{categoryId}

카테고리 단건 조회

---

### GET /api/categories/tree

카테고리 트리 조회

---

### POST /api/categories

카테고리 생성 | **Auth: JWT**

**Request Body**

```json
{
  "name": "string",
  "parentId": "long?",
  "ordering": "int? (default: 0)",
  "idempotencyKey": "string?"
}
```

---

### POST /api/categories/tree

카테고리 트리 생성 (재귀) | **Auth: JWT**

**Request Body**

```json
{
  "name": "string",
  "ordering": "int? (default: 0)",
  "children": [
    {
      "name": "string",
      "ordering": "int?",
      "children": []
    }
  ],
  "idempotencyKey": "string?"
}
```

---

### GET /api/brands

브랜드 목록 조회

---

### GET /api/brands/{brandId}

브랜드 상세 조회

---

### POST /api/brands

브랜드 등록 | **Auth: JWT**

**Request Body**

```json
{
  "name": "string",
  "logoImageUrl": "string?",
  "idempotencyKey": "string?"
}
```

---

### PUT /api/brands/{brandId}

브랜드 수정 | **Auth: JWT**

**Request Body**

```json
{
  "name": "string?",
  "logoImageUrl": "string?"
}
```

---

### DELETE /api/brands/{brandId}

브랜드 삭제 | **Auth: JWT**

---

### POST /api/reviews

리뷰 작성 | **Auth: @AuthId**

**Request Body**

```json
{
  "productId": "long",
  "orderItemId": "long?",
  "title": "string",
  "content": "string",
  "rating": "int (1-5)",
  "imageUrls": [
    "string"
  ],
  "idempotencyKey": "string?"
}
```

---

### GET /api/products/{productId}/reviews

상품별 리뷰 조회

**Query Parameters**

| Name | Type | Description          |
|------|------|----------------------|
| page | Int  | 페이지 번호 (default: 0)  |
| size | Int  | 페이지 크기 (default: 20) |

---

### PUT /api/reviews/{reviewId}

리뷰 수정 | **Auth: @AuthId**

**Request Body**

```json
{
  "title": "string?",
  "content": "string?",
  "rating": "int? (1-5)"
}
```

---

### DELETE /api/reviews/{reviewId}

리뷰 삭제 | **Auth: @AuthId**

---

### POST /api/reviews/{reviewId}/like

리뷰 좋아요 토글 | **Auth: @AuthId**

**Response**

```json
{
  "liked": "boolean"
}
```

---

### POST /api/snaps

스냅 작성 | **Auth: @AuthId**

**Request Body**

```json
{
  "productId": "long",
  "caption": "string?",
  "imageUrls": [
    "string"
  ],
  "idempotencyKey": "string?"
}
```

---

### GET /api/snaps

스냅 피드 조회

**Query Parameters**

| Name | Type | Description          |
|------|------|----------------------|
| page | Int  | 페이지 번호 (default: 0)  |
| size | Int  | 페이지 크기 (default: 20) |

---

### PUT /api/snaps/{snapId}

스냅 수정 | **Auth: @AuthId**

**Request Body**

```json
{
  "caption": "string?"
}
```

---

### DELETE /api/snaps/{snapId}

스냅 삭제 | **Auth: @AuthId**

---

### POST /api/snaps/{snapId}/like

스냅 좋아요 토글 | **Auth: @AuthId**

**Response**

```json
{
  "liked": "boolean"
}
```

---

## Inventory Service

### GET /api/inventories/{skuId}

재고 조회

**Response**

```json
{
  "skuId": "string",
  "totalStock": "int",
  "reservedStock": "int",
  "availableStock": "int"
}
```

---

### POST /api/inventories/bulk

대량 재고 조회

**Request Body**

```json
{
  "skuIds": [
    "string"
  ]
}
```

---

### POST /api/inventories/increase

대량 재고 추가

**Request Body**

```json
{
  "items": [
    {
      "skuId": "string",
      "quantity": "int"
    }
  ],
  "idempotencyKey": "string?"
}
```

---

### POST /api/inventories/decrease

대량 재고 감소

**Request Body**

```json
{
  "items": [
    {
      "skuId": "string",
      "quantity": "int"
    }
  ],
  "idempotencyKey": "string?"
}
```

---

### GET /api/inventories/logs

재고 변경 로그 조회 | **Auth: ADMIN**

**Query Parameters**

| Name  | Type           | Description      |
|-------|----------------|------------------|
| skuId | String         | SKU ID           |
| from  | LocalDateTime? | 시작 일시 (ISO 8601) |
| to    | LocalDateTime? | 종료 일시 (ISO 8601) |

**Response**

```json
{
  "logs": [
    {
      "id": "long",
      "skuId": "string",
      "orderId": "long?",
      "action": "InventoryAction",
      "quantity": "int",
      "createdAt": "LocalDateTime"
    }
  ]
}
```

---

## Order Service

### POST /api/orders

주문 생성 | **Auth: JWT**

**Request Body**

```json
{
  "idempotencyKey": "string?",
  "items": [
    {
      "skuId": "long",
      "productId": "long",
      "brandId": "long",
      "titleSnapshot": "string",
      "optionSnapshot": "string?",
      "quantity": "int (positive)",
      "unitPrice": "long (positive)"
    }
  ],
  "discountAmount": "long (default: 0, >= 0)",
  "shippingFee": "long (default: 0, >= 0)",
  "shippingAddress": {
    "recipient": "string",
    "phone": "string",
    "zipCode": "string",
    "address": "string",
    "addressDetail": "string"
  }
}
```

**Response**

```json
{
  "orderId": "long",
  "orderNo": "string",
  "status": "OrderStatus",
  "totalAmount": "long"
}
```

---

### GET /api/orders

주문 목록 조회 | **Auth: @AuthId**

**Query Parameters**

| Name | Type | Description          |
|------|------|----------------------|
| page | Int  | 페이지 번호 (default: 0)  |
| size | Int  | 페이지 크기 (default: 20) |

---

### GET /api/orders/{orderId}

주문 상세 조회

**Response**

```json
{
  "orderId": "long",
  "orderNo": "string",
  "userId": "long",
  "status": "OrderStatus",
  "subtotalAmount": "long",
  "discountAmount": "long",
  "shippingFee": "long",
  "totalAmount": "long",
  "currency": "string",
  "shippingAddressSnapshot": "string",
  "items": [
    {
      "itemId": "long",
      "skuId": "long",
      "productId": "long",
      "brandId": "long",
      "titleSnapshot": "string",
      "optionSnapshot": "string?",
      "qty": "int",
      "unitPrice": "long",
      "lineAmount": "long"
    }
  ],
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime",
  "placedAt": "LocalDateTime?",
  "paidAt": "LocalDateTime?",
  "canceledAt": "LocalDateTime?"
}
```

---

### POST /api/orders/{orderId}/cancel

주문 취소 | **Auth: @AuthId**

---

### GET /api/carts/me

장바구니 조회 | **Auth: @AuthId**

**Response**

```json
{
  "cartId": "long",
  "items": [
    {
      "cartItemId": "long",
      "skuId": "long",
      "qty": "int"
    }
  ]
}
```

---

### POST /api/carts/me/items

장바구니 아이템 추가 | **Auth: @AuthId**

**Request Body**

```json
{
  "skuId": "long",
  "qty": "int (positive)",
  "idempotencyKey": "string?"
}
```

---

### PATCH /api/carts/me/items/{cartItemId}

장바구니 아이템 수량 수정 | **Auth: @AuthId**

**Request Body**

```json
{
  "qty": "int (positive)"
}
```

---

### DELETE /api/carts/me/items/{cartItemId}

장바구니 아이템 제거 | **Auth: @AuthId**

---

### DELETE /api/carts/me/items

장바구니 전체 삭제 | **Auth: @AuthId**

---

## Payment Service

### POST /api/payments/confirm

결제 승인

**Request Body**

```json
{
  "orderId": "long",
  "amount": "string",
  "paymentKey": "string"
}
```

**Response**

```json
{
  "success": "boolean"
}
```

---

## Common Response Format

모든 API 응답은 `ApiResponse<T>`로 래핑됩니다.

**성공**

```json
{
  "status": "SUCCESS",
  "data": {
    ...
  }
}
```

**실패**

```json
{
  "status": "ERROR",
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}
```

## Authentication

| Type    | Description                           |
|---------|---------------------------------------|
| JWT     | `Authorization: Bearer <token>` 헤더 필요 |
| @AuthId | JWT 토큰에서 추출한 사용자 ID를 컨트롤러에 주입         |
| ADMIN   | ADMIN 역할이 필요한 관리자 전용 엔드포인트            |
| -       | 인증 불필요                                |
