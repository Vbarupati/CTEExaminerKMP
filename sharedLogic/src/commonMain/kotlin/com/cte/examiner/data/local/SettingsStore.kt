package com.cte.examiner.data.local

import com.cte.examiner.domain.storage.SettingsStorage
import com.cte.examiner.domain.storage.TokenStorage
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class)
class SettingsStore(
    private val settings: ObservableSettings,
) : TokenStorage, SettingsStorage {

    private object Keys {
        const val BASE_URL = "base_url"
        const val TOKEN = "token"
        const val EXAMINER_NAME = "examiner_name"
        const val SESSION_JSON = "session_json"
    }

    // ---- SettingsStorage ----
    override val baseUrl: Flow<String> =
        settings.getStringFlow(Keys.BASE_URL, DEFAULT_BASE_URL)

    override suspend fun setBaseUrl(url: String) {
        settings[Keys.BASE_URL] = url.trim()
    }

    // ---- TokenStorage ----
    override val token: Flow<String?> =
        settings.getStringOrNullFlow(Keys.TOKEN)

    override val examinerName: Flow<String> =
        settings.getStringFlow(Keys.EXAMINER_NAME, "")

    override val sessionJson: Flow<String?> =
        settings.getStringOrNullFlow(Keys.SESSION_JSON)

    override suspend fun saveSession(token: String, examinerName: String, sessionJson: String) {
        settings[Keys.TOKEN] = token
        settings[Keys.EXAMINER_NAME] = examinerName
        settings[Keys.SESSION_JSON] = sessionJson
    }

    override suspend fun clearSession() {
        settings.remove(Keys.TOKEN)
        settings.remove(Keys.EXAMINER_NAME)
        settings.remove(Keys.SESSION_JSON)
    }

    companion object {
        const val DEFAULT_BASE_URL = "http://172.23.114.3/sites/cte/"
    }
}