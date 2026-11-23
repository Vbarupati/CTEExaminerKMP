package com.cte.examiner.data.repository

import com.cte.examiner.data.local.SettingsStore
import com.cte.examiner.data.remote.NetworkConstants
import com.cte.examiner.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val settings: SettingsStore,
) : SettingsRepository {

    override val baseUrl: Flow<String> = settings.baseUrl
    override val examinerName: Flow<String> = settings.examinerName

    override suspend fun setBaseUrl(url: String): Result<Unit> = runCatching {
        settings.setBaseUrl(url.normalizeUrl())
    }

    override suspend fun resetBaseUrl(): Result<Unit> = runCatching {
        settings.setBaseUrl(NetworkConstants.DEFAULT_BASE_URL)
    }

    /** Trim, ensure a scheme, and end with a single trailing slash. */
    private fun String.normalizeUrl(): String {
        var u = trim()
        if (u.isEmpty()) return NetworkConstants.DEFAULT_BASE_URL
        if (!u.startsWith("http://") && !u.startsWith("https://")) {
            u = "http://$u"
        }
        return u.trimEnd('/') + "/"
    }
}