package com.koosco.commonsecurity.resolver

/**
 * 컨트롤러 메서드 파라미터에 SecurityContext의 사용자 ID를 주입하기 위한 애노테이션
 *
 * @sample
 * ```kotlin
 * @GetMapping("/profile")
 * fun getProfile(@AuthId userId: Long?): ResponseEntity<UserProfile> {
 *     // userId가 null이면 인증되지 않은 요청
 * }
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AuthId
