package com.cte.examiner.domain.storage

import kotlinx.coroutines.flow.Flow

interface SettingsStorage {
    val baseUrl: Flow<String>
    suspend fun setBaseUrl(url: String)
}