package com.cte.examiner.domain.storage

import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    val token: Flow<String?>
    val examinerName: Flow<String>
    val sessionJson: Flow<String?>
    suspend fun saveSession(token: String, examinerName: String, sessionJson: String)
    suspend fun clearSession()
}