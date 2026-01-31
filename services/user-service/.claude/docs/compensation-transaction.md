# 보상 트랜잭션 패턴

회원가입 시 User/Auth Service 간 데이터 일관성 보장:

```kotlin
@UseCase
class RegisterUseCase(...) {
    fun execute(command: CreateUserCommand) {
        // 1. User 저장
        val user = transactionRunner.run { registerUser(command) }

        // 2. Auth Service 호출
        try {
            authServiceClient.notifyUserCreated(...)
        } catch (ex: Exception) {
            // 3. 실패 시 보상 트랜잭션 (User 삭제)
            transactionRunner.runNew { deleteById(user.id!!) }
            throw ExternalServiceException(...)
        }
    }
}
```

**Kafka 이벤트 없음** - Feign Client로 동기 통신:

```kotlin
@FeignClient(name = "auth-service", url = "\${auth-service.url}")
interface AuthClient {
    @PostMapping("/api/auth")
    fun createUser(@RequestBody request: CreateUserRequest)
}
```
