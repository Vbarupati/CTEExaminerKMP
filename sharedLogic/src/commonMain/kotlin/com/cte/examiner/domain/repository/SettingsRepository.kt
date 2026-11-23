package com.cte.examiner.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    /** The current API base URL, updates as it changes. */
    val baseUrl: Flow<String>

    /** The signed-in examiner's display name (empty if none). */
    val examinerName: Flow<String>

    /** Validates/normalizes and stores a new base URL. */
    suspend fun setBaseUrl(url: String): Result<Unit>

    /** Restores the default base URL. */
    suspend fun resetBaseUrl(): Result<Unit>
}