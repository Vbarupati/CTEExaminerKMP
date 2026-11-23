package com.cte.examiner.domain.usecase

import com.cte.examiner.domain.model.Examinee
import com.cte.examiner.domain.model.UserInfo
import com.cte.examiner.domain.repository.AuthRepository
import com.cte.examiner.domain.repository.ExamineeRepository
import com.cte.examiner.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use cases = one action each. The UI (via a Store) calls these,
 * never the repositories directly. Keeps logic testable and shared.
 */

// ---------------- Auth ----------------

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> {
        val user = username.trim()
        if (user.isBlank()) return Result.failure(IllegalArgumentException("Username is required"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password is required"))
        return authRepository.login(user, password)
    }
}

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = authRepository.logout()
}

class ObserveLoginStateUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = authRepository.isLoggedIn()
}

// ---------------- Examinee / Asset ----------------

class GetExamineeUseCase(
    private val examineeRepository: ExamineeRepository
) {
    /**
     * Accepts either a raw qcode or a full scanned URL/QR payload.
     * Extracts the qcode before calling the repository.
     */
    suspend operator fun invoke(scannedValue: String): Result<Examinee> {
        val qcode = scannedValue.extractQcode()
        if (qcode.isBlank()) {
            return Result.failure(IllegalArgumentException("Could not read a valid code"))
        }
        return examineeRepository.getExaminee(qcode)
    }
}

class GetUserInfoUseCase(
    private val examineeRepository: ExamineeRepository
) {
    suspend operator fun invoke(): Result<UserInfo> = examineeRepository.getUserInfo()
}

// ---------------- Settings (base URL) ----------------

class GetBaseUrlUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<String> = settingsRepository.baseUrl
}

class SetBaseUrlUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(url: String): Result<Unit> {
        if (url.isBlank()) return Result.failure(IllegalArgumentException("URL is required"))
        return settingsRepository.setBaseUrl(url)
    }
}

// ---------------- Helper ----------------

/**
 * Pulls the qcode out of a scanned value. Handles:
 *   - "?qcode=ABC123" or "?quid=ABC123"  (query param, any case)
 *   - "https://host/path/ABC123"          (last path segment)
 *   - "ABC123"                            (already a bare code)
 */
internal fun String.extractQcode(): String {
    val value = trim()
    if (value.isEmpty()) return ""

    // 1) query param ?qcode= or ?quid=
    Regex("""[?&](?:qcode|quid)=([^&\s]+)""", RegexOption.IGNORE_CASE)
        .find(value)
        ?.groupValues?.get(1)
        ?.let { return it }

    // 2) last path segment of a URL
    if (value.contains("/")) {
        val lastSegment = value
            .substringBefore('?')
            .trimEnd('/')
            .substringAfterLast('/')
        if (lastSegment.isNotBlank()) return lastSegment
    }

    // 3) already a bare code
    return value
}