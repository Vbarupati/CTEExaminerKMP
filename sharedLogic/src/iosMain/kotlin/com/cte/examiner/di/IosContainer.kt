package com.cte.examiner.di

import com.cte.examiner.presentation.home.HomeState
import com.cte.examiner.presentation.home.HomeStore
import com.cte.examiner.presentation.login.LoginState
import com.cte.examiner.presentation.login.LoginStore
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

// ---------------- Swift-facing wrappers ----------------

class LoginStoreIos(private val store: LoginStore) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val current: LoginState get() = store.state.value
    fun observe(onEach: (LoginState) -> Unit): Job = store.state.onEach { onEach(it) }.launchIn(scope)
    fun login() = store.login()
    fun dispose() { scope.cancel() }
}

class HomeStoreIos(private val store: HomeStore) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val current: HomeState get() = store.state.value
    fun observe(onEach: (HomeState) -> Unit): Job = store.state.onEach { onEach(it) }.launchIn(scope)
    fun scan(qrValue: String) = store.scan(qrValue)
    fun clearResult() = store.clearResult()
    fun logout(onDone: () -> Unit) = store.logout(onDone)
    fun dispose() { scope.cancel() }
}

class SettingsIos(private val appContainer: AppContainer) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    fun observeBaseUrl(onEach: (String) -> Unit): Job =
        appContainer.getBaseUrl().onEach { onEach(it) }.launchIn(scope)
    fun setBaseUrl(url: String, onDone: (String?) -> Unit) {
        scope.launch {
            appContainer.setBaseUrl(url).onSuccess { onDone(null) }.onFailure { onDone(it.message) }
        }
    }
    fun dispose() { scope.cancel() }
}