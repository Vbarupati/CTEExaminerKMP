package com.cte.examiner.di

import com.cte.examiner.presentation.home.HomeStore
import com.cte.examiner.presentation.login.LoginStore
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import platform.Foundation.NSUserDefaults

/**
 * Single entry point for the iOS app.
 *
 * Swift side:
 *   let container = IosContainer()
 *   let loginVM   = LoginViewModel(store: container.makeLoginStore())
 *   ...
 *   container.close()   // on app teardown
 */
class IosContainer {

    // iOS key-value storage backing SettingsStore
    private val settings: ObservableSettings =
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)

    // Shared DI graph (repositories, use cases, Ktor client, etc.)
    private val appContainer = AppContainer(settings)

    // One scope owned by the container; SwiftUI stores run on it.
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

    /** Cancel all running coroutines. Call from the iOS app lifecycle teardown. */
    fun close() {
        scope.cancel()
    }
}