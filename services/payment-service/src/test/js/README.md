# Toss Payments 테스트 페이지

Toss Payments v2 API를 사용한 결제 연동 테스트 페이지입니다.

## 📁 파일 구조

```
src/test/js/
└── index.html    # Toss Payments 결제 테스트 페이지
```

## 🎯 개요

이 테스트 페이지는 Toss Payments의 Payment Widget v2 API를 사용하여 결제 기능을 테스트할 수 있는 독립 실행형 HTML 페이지입니다. 실제 결제가 진행되지 않는 테스트 모드로 동작합니다.

## 🚀 주요 기능

### 1. 결제 위젯 초기화
- **SDK**: Toss Payments v2 Standard SDK
- **방식**: `TossPayments()` → `widgets()` 인스턴스 생성
- **고객 식별**: 타임스탬프 기반 고유 customerKey 자동 생성

### 2. 결제 UI 렌더링
- **결제 수단 선택**: 카드, 간편결제, 계좌이체 등 다양한 결제 수단
- **약관 동의**: 필수 약관 동의 UI 자동 렌더링
- **반응형 디자인**: 모바일/데스크톱 환경 모두 지원

### 3. 입력 값 검증
- 최소 결제 금액: 1,000원 이상
- 필수 정보: 주문명, 구매자명, 이메일
- 실시간 유효성 검사 및 에러 메시지 표시

### 4. 결제 프로세스
- 고유 주문 ID 자동 생성 (타임스탬프 기반)
- 결제 성공/실패 URL 리다이렉트 처리
- URL 파라미터를 통한 결제 결과 확인

## 🔑 테스트 자격 증명

```javascript
// 결제위젯 연동 키 (test_gck로 시작)
const clientKey = 'test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm';
const secretKey = 'test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6'; // 서버에서 사용

// 고객 식별자 (자동 생성)
const customerKey = 'test_customer_' + Date.now();
```

⚠️ **중요**:
- 이 키는 테스트 전용입니다. 실제 운영 환경에서는 별도의 운영 키를 사용해야 합니다.
- **결제위젯 연동 키** (`test_gck_`)를 사용해야 하며, **API 개별 연동 키** (`test_ck_`)는 사용할 수 없습니다.
- 시크릿 키는 클라이언트에 노출되면 안 되며, 서버에서만 사용해야 합니다.

### API 키 종류

Toss Payments는 두 가지 유형의 API 키를 제공합니다:

| 키 타입 | 클라이언트 키 형식 | 시크릿 키 형식 | 용도 |
|---------|-------------------|----------------|------|
| **결제위젯 연동 키** | `test_gck_*` / `live_gck_*` | `test_gsk_*` / `live_gsk_*` | Payment Widget 전용 |
| **API 개별 연동 키** | `test_ck_*` / `live_ck_*` | `test_sk_*` / `live_sk_*` | 결제창, 브랜드페이, 빌링 등 |

💡 **본 테스트 페이지는 Payment Widget을 사용하므로 결제위젯 연동 키(`test_gck_`)를 사용합니다.**

## 📋 사용 방법

### 1. 페이지 실행

```bash
# 웹 브라우저로 index.html 파일을 직접 열거나
open src/test/js/index.html

# 로컬 서버를 사용하는 경우
cd src/test/js
python3 -m http.server 8000
# 브라우저에서 http://localhost:8000 접속
```

### 2. 결제 테스트

1. **결제 정보 입력**
   - 결제 금액: 기본값 10,000원 (수정 가능, 최소 1,000원)
   - 주문명: 기본값 "테스트 상품"
   - 구매자명: 기본값 "홍길동"
   - 이메일: 기본값 "test@example.com"

2. **결제 수단 선택**
   - 자동 렌더링된 결제 수단 중 선택
   - 테스트 모드에서는 모든 결제 수단 사용 가능

3. **약관 동의**
   - 필수 약관에 동의

4. **결제 진행**
   - "결제하기" 버튼 클릭
   - Toss Payments 결제창에서 테스트 진행

### 3. 결제 결과 확인

**성공 시:**
```
URL: ?success=true&paymentKey=xxx&orderId=xxx&amount=xxx
화면: 초록색 성공 메시지 표시
```

**실패 시:**
```
URL: ?fail=true&code=xxx&message=xxx
화면: 빨간색 에러 메시지 표시
```

## 🏗️ 기술 구현

### SDK 로드 및 초기화

```javascript
// 1. TossPayments SDK 초기화
const tossPayments = TossPayments(clientKey);

// 2. 결제위젯 인스턴스 생성
paymentWidget = tossPayments.widgets({
  customerKey: customerKey
});
```

### 결제 UI 렌더링

```javascript
// 결제 금액 설정
await paymentWidget.setAmount({
  currency: 'KRW',
  value: amount
});

// 결제 수단 UI 렌더링
await paymentWidget.renderPaymentMethods({
  selector: '#payment-method',
  variantKey: 'DEFAULT'
});

// 약관 동의 UI 렌더링
await paymentWidget.renderAgreement({
  selector: '#agreement',
  variantKey: 'AGREEMENT'
});
```

### 결제 요청

```javascript
await paymentWidget.requestPayment({
  orderId: orderId,              // 고유 주문 ID
  orderName: orderName,          // 주문명
  successUrl: successUrl,        // 성공 리다이렉트 URL
  failUrl: failUrl,              // 실패 리다이렉트 URL
  customerName: customerName,    // 구매자명
  customerEmail: customerEmail   // 구매자 이메일
});
```

## 🎨 UI/UX 특징

### 디자인 시스템
- **폰트**: Apple 시스템 폰트 기반
- **컬러 스키마**:
  - Primary: `#3182f6` (Toss Blue)
  - Success: `#4caf50` (Green)
  - Error: `#f44336` (Red)
- **레이아웃**: 중앙 정렬, 최대 너비 600px

### 사용자 피드백
- 버튼 호버 효과
- 에러 메시지 5초 자동 숨김
- 로딩 상태 표시
- 실시간 입력 검증

## 🔒 보안 고려사항

### 현재 구현 (테스트용)
```javascript
// ⚠️ 클라이언트 측에 키가 노출됨 (테스트용으로만 사용)
const clientKey = 'test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq';
```

### 운영 환경 권장사항
1. **클라이언트 키 관리**
   - 환경 변수 또는 서버 측 설정으로 관리
   - 운영 키(`live_ck_`)와 테스트 키(`test_ck_`) 분리

2. **결제 승인 프로세스**
   ```
   [클라이언트] → 결제 요청 → [Toss Payments]
                                      ↓
   [서버] ← 결제 승인 API 호출 ← [Toss Payments]
   ```
   - 실제 운영에서는 서버에서 결제 승인 API를 호출해야 함
   - 클라이언트만으로는 결제가 완료되지 않음

3. **데이터 검증**
   - 서버 측에서 결제 금액, 주문 정보 재검증 필수
   - 클라이언트 측 검증은 UX 개선용

## 📚 API 레퍼런스

### TossPayments()
```typescript
TossPayments(clientKey: string): TossPaymentsInstance
```
- TossPayments SDK 초기화
- **clientKey**: 클라이언트 키 (test_ck_ 또는 live_ck_로 시작)

### widgets()
```typescript
widgets(options: WidgetOptions): PaymentWidget
```
- 결제위젯 인스턴스 생성
- **options.customerKey**: 고유 고객 식별자

### setAmount()
```typescript
setAmount(amount: AmountOptions): Promise<void>
```
- 결제 금액 설정
- **amount.currency**: 통화 (예: 'KRW')
- **amount.value**: 금액 (숫자)

### renderPaymentMethods()
```typescript
renderPaymentMethods(options: RenderOptions): Promise<void>
```
- 결제 수단 UI 렌더링
- **options.selector**: 렌더링할 DOM 선택자
- **options.variantKey**: UI 변형 키 (기본값: 'DEFAULT')

### renderAgreement()
```typescript
renderAgreement(options: RenderOptions): Promise<void>
```
- 약관 동의 UI 렌더링
- **options.selector**: 렌더링할 DOM 선택자
- **options.variantKey**: UI 변형 키 (기본값: 'AGREEMENT')

### requestPayment()
```typescript
requestPayment(paymentInfo: PaymentInfo): Promise<void>
```
- 결제 요청
- **paymentInfo.orderId**: 고유 주문 ID
- **paymentInfo.orderName**: 주문명
- **paymentInfo.successUrl**: 성공 리다이렉트 URL
- **paymentInfo.failUrl**: 실패 리다이렉트 URL
- **paymentInfo.customerName**: 구매자명
- **paymentInfo.customerEmail**: 구매자 이메일

## 🐛 문제 해결

### "API 개별 연동 키는 지원하지 않습니다" 에러

**원인**: 잘못된 API 키 타입 사용

**에러 메시지**:
```
결제 시스템을 초기화하는데 실패했습니다: 결제위젯 연동 키의 클라이언트 키로 SDK를 연동해주세요.
API 개별 연동 키는 지원하지 않습니다.
```

**해결**:
```javascript
// ❌ 잘못된 키 (API 개별 연동 키)
const clientKey = 'test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq';

// ✅ 올바른 키 (결제위젯 연동 키)
const clientKey = 'test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm';
```

### 결제 UI가 렌더링되지 않는 경우

**원인**: SDK 로딩 실패 또는 초기화 오류

**해결**:
1. 브라우저 콘솔에서 에러 메시지 확인
2. 네트워크 탭에서 SDK 스크립트 로딩 확인
3. 클라이언트 키가 `test_gck_`로 시작하는지 확인

### 금액 업데이트가 안 되는 경우

**원인**: `paymentWidget` 인스턴스가 초기화되지 않음

**해결**:
```javascript
// paymentWidget이 null인지 확인
if (paymentWidget) {
  await paymentWidget.setAmount({ currency: 'KRW', value: amount });
}
```

### CORS 에러 발생

**원인**: 로컬 파일로 직접 열 때 발생 가능

**해결**:
```bash
# 로컬 웹 서버 사용
python3 -m http.server 8000
# 또는
npx serve .
```

## 📖 참고 문서

### 공식 문서
- [Toss Payments v2 연동 가이드](https://docs.tosspayments.com/guides/v2/payment-widget/integration)
- [JavaScript SDK 레퍼런스](https://docs.tosspayments.com/sdk/v2/js)
- [API 인증 가이드](https://docs.tosspayments.com/reference/using-api/authorization)
- [API 요청/응답 형식](https://docs.tosspayments.com/reference/using-api/req-res)

### 관련 리소스
- [토스페이먼츠 결제 연동하기](https://velog.io/@tosspayments/토스페이먼츠-결제-연동하기)
- [토스페이먼츠 SDK v2 소개](https://docs.tosspayments.com/blog/tosspayments-sdk-v2)

## 🔄 버전 정보

- **SDK 버전**: v2 (Standard)
- **SDK URL**: `https://js.tosspayments.com/v2/standard`
- **작성일**: 2025-12-24
- **테스트 클라이언트 키**: `test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm` (결제위젯 연동 키)
- **테스트 시크릿 키**: `test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6` (서버 전용)

## 📝 다음 단계

### 운영 환경 준비
1. **서버 측 결제 승인 API 구현**
   - `/api/payments/confirm` 엔드포인트 생성
   - Toss Payments 결제 승인 API 호출
   - 결제 결과 데이터베이스 저장

2. **환경 변수 설정**
   ```env
   TOSS_CLIENT_KEY=live_ck_xxxxxxxxxxxxx
   TOSS_SECRET_KEY=live_sk_xxxxxxxxxxxxx
   ```

3. **웹훅 설정**
   - Toss Payments 개발자 센터에서 웹훅 URL 등록
   - 결제 상태 변경 이벤트 처리

4. **보안 강화**
   - HTTPS 적용
   - CSP (Content Security Policy) 설정
   - 결제 금액 서버 측 검증

### 추가 기능 구현
- [ ] 결제 내역 조회
- [ ] 결제 취소/환불 처리
- [ ] 정기 결제 (빌링) 연동
- [ ] 다국어 지원
- [ ] 다중 통화 지원

## ⚠️ 주의사항

1. **테스트 전용**: 현재 코드는 테스트 목적으로만 사용
2. **운영 배포 금지**: 실제 운영 환경에 그대로 배포 불가
3. **서버 검증 필수**: 클라이언트 측 검증만으로는 보안 불충분
4. **키 관리**: 시크릿 키는 절대 클라이언트에 노출 금지
5. **금액 검증**: 결제 금액은 반드시 서버에서 재검증

## 📞 지원

문제가 발생하거나 질문이 있는 경우:
- [Toss Payments 개발자 센터](https://developers.tosspayments.com/)
- [Toss Payments GitHub Issues](https://github.com/tosspayments)
- [개발자 커뮤니티](https://developers.tosspayments.com/community)
