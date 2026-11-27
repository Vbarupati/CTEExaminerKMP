package com.cte.examiner.di

import com.cte.examiner.presentation.home.HomeStore
import com.cte.examiner.presentation.login.LoginStore
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import platform.Foundation.NSUserDefaults

class IosContainer {
    private val settings: ObservableSettings =
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    private val appContainer = AppContainer(settings)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun makeLoginStore(): LoginStoreIos = LoginStoreIos(
        LoginStore(scope, appContainer.loginUseCase, appContainer.observeLoginState)
    )
    fun makeHomeStore(): HomeStoreIos = HomeStoreIos(
        HomeStore(scope, appContainer.getUserInfo, appContainer.getExaminee, appContainer.logoutUseCase)
    )
    fun makeSettings(): SettingsIos = SettingsIos(appContainer)
}
