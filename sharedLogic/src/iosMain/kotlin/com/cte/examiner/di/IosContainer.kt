package com.cte.examiner.di

import com.cte.examiner.presentation.home.HomeStore
import com.cte.examiner.presentation.login.LoginStore
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import platform.Foundation.NSUserDefaults

class IosContainer {

    private val settings: ObservableSettings =
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)

    private val appContainer = AppContainer(settings)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun makeLoginStore(): LoginStore = LoginStore(
        scope = scope,
        loginUseCase = appContainer.loginUseCase,
        observeLoginState = appContainer.observeLoginState
    )

    fun makeHomeStore(): HomeStore = HomeStore(
        scope = scope,
        getUserInfo = appContainer.getUserInfo,
        getExaminee = appContainer.getExaminee,
        logoutUseCase = appContainer.logoutUseCase
    )

    // Settings
    fun getBaseUrl(): Flow<String> = appContainer.getBaseUrl()
    suspend fun setBaseUrl(url: String) { appContainer.setBaseUrl(url) }

    fun close() { scope.cancel() }
}