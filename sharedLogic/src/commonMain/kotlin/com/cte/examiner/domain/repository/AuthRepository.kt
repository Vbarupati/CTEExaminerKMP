package com.cte.examiner.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Authenticates and persists the session on success. */
    suspend fun login(username: String, password: String): Result<Unit>

    /** Clears the stored session/token. */
    suspend fun logout(): Result<Unit>

    /** Emits true while a valid session/token is stored. */
    fun isLoggedIn(): Flow<Boolean>
}