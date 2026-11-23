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

class AppContainer(settings: ObservableSettings) {

    private val settingsStore = SettingsStore(settings)

    private val httpClient = createHttpClient(
        getToken = { settingsStore.token.first() },
        getHeaders = { buildSessionHeaders() }
    )

    private val api = CteApi(httpClient)

    // Repositories
    private val authRepository = AuthRepositoryImpl(api, settingsStore)
    private val examineeRepository = ExamineeRepositoryImpl(api, settingsStore)
    private val settingsRepository = SettingsRepositoryImpl(settingsStore)

    // Use cases — ViewModels / stores read these
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
        val json = settingsStore.sessionJson.first()?.toJsonObjectOrNull() ?: return headers
        json.findString("userProfileId", "UserId")?.let { headers["UserId"] = it }
        json.findString("subsNum", "SubsNum")?.let { headers["SubsNum"] = it }
        return headers
    }
}