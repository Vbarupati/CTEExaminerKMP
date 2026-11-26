package com.cte.examiner.di

import com.cte.examiner.data.local.SettingsStore
import com.cte.examiner.data.remote.CteApi
import com.cte.examiner.data.remote.createHttpClient
import com.cte.examiner.data.remote.findString
import com.cte.examiner.data.remote.toJsonObjectOrNull
import com.cte.examiner.data.repository.AuthRepositoryImpl
import com.cte.examiner.data.repository.ExamineeRepositoryImpl
import com.cte.examiner.data.repository.SettingsRepositoryImpl
import com.cte.examiner.domain.usecase.GetBaseUrlUseCase
import com.cte.examiner.domain.usecase.GetExamineeUseCase
import com.cte.examiner.domain.usecase.GetUserInfoUseCase
import com.cte.examiner.domain.usecase.LoginUseCase
import com.cte.examiner.domain.usecase.LogoutUseCase
import com.cte.examiner.domain.usecase.ObserveLoginStateUseCase
import com.cte.examiner.domain.usecase.SetBaseUrlUseCase
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonObject

class AppContainer(settings: ObservableSettings) {

    private val settingsStore = SettingsStore(settings)

    private val httpClient = createHttpClient(
        getToken = { settingsStore.token.first() },
        getHeaders = { buildSessionHeaders() }
    )

    private val api = CteApi(httpClient)

    private val authRepository = AuthRepositoryImpl(api, settingsStore)
    private val examineeRepository = ExamineeRepositoryImpl(api, settingsStore)
    private val settingsRepository = SettingsRepositoryImpl(settingsStore)

    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val observeLoginState = ObserveLoginStateUseCase(authRepository)
    val getExaminee = GetExamineeUseCase(examineeRepository)
    val getUserInfo = GetUserInfoUseCase(examineeRepository)
    val getBaseUrl = GetBaseUrlUseCase(settingsRepository)
    val setBaseUrl = SetBaseUrlUseCase(settingsRepository)

    /** Session headers (UserId, SubsNum, api-version) added to every request after login. */
    private suspend fun buildSessionHeaders(): Map<String, String> {
        val headers = mutableMapOf("api-version" to "1.0")
        val json = settingsStore.sessionJson.first()?.toJsonObjectOrNull() ?: run {
            println("CTE_HEADERS: $headers (no session json)")
            return headers
        }

        // subsNum / userProfileId may be top-level OR inside the merged "person" object
        val person = json["person"] as? JsonObject

        (json.findString("userProfileId", "UserId") ?: person?.findString("userProfileId", "UserId"))
            ?.let { headers["UserId"] = it }

        (json.findString("subsNum", "SubsNum") ?: person?.findString("subsNum", "SubsNum"))
            ?.let { headers["SubsNum"] = it }

        println("CTE_HEADERS: $headers")      // ← paste this after re-login
        return headers
    }
}