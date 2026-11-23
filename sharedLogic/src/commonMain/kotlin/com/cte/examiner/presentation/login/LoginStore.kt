package com.cte.examiner.presentation.login

import com.cte.examiner.domain.usecase.LoginUseCase
import com.cte.examiner.domain.usecase.ObserveLoginStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class LoginStore(
    private val scope: CoroutineScope,
    private val loginUseCase: LoginUseCase,
    private val observeLoginState: ObserveLoginStateUseCase
) {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        scope.launch {
            observeLoginState().collect { loggedIn ->
                _state.update { it.copy(isLoggedIn = loggedIn) }
            }
        }
    }

    fun onUsernameChange(value: String) {
        _state.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        println("LOGIN tapped: user=${_state.value.username}, canSubmit=${_state.value.canSubmit}")

        val current = _state.value
        if (!current.canSubmit) return

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        scope.launch {
            println("LOGIN calling useCase...")
            val result = loginUseCase(current.username, current.password)
            result
                .onSuccess {
                    println("LOGIN success")
                    _state.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                .onFailure { error ->
                    println("LOGIN failed: ${error.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Login failed. Please try again."
                        )
                    }
                }
        }
    }

    fun consumeError() {
        _state.update { it.copy(errorMessage = null) }
    }
}