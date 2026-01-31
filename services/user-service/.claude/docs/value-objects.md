# User Service Value Objects

## Email VO

정규식 기반 이메일 형식 검증:

```kotlin
@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun of(rawEmail: String?): Email  // 검증 후 생성
    }
}
```

## Phone VO

한국 휴대폰 형식 (`010-XXXX-XXXX`):

```kotlin
@JvmInline
value class Phone(val value: String?)  // nullable, 형식 검증
```

## JPA Converter

`@Converter(autoApply = true)`로 VO 자동 영속화
