package com.cte.examiner.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cte.examiner.di.AppContainer
import com.cte.examiner.presentation.login.LoginStore

class LoginViewModel(container: AppContainer) : ViewModel() {
    val store = LoginStore(
        scope = viewModelScope,
        loginUseCase = container.loginUseCase,
        observeLoginState = container.observeLoginState
    )
}