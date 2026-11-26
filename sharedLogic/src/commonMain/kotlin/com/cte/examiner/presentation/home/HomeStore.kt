package com.cte.examiner.presentation.home

import com.cte.examiner.domain.error.DomainError
import com.cte.examiner.domain.model.Examinee
import com.cte.examiner.domain.model.UserInfo
import com.cte.examiner.domain.usecase.GetExamineeUseCase
import com.cte.examiner.domain.usecase.GetUserInfoUseCase
import com.cte.examiner.domain.usecase.LogoutUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class HomeStore(
    private val scope: CoroutineScope,
    private val getUserInfo: GetUserInfoUseCase,
    private val getExaminee: GetExamineeUseCase,
    private val logoutUseCase: LogoutUseCase,
) {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init { loadUser() }

    private fun loadUser() {
        scope.launch {
            getUserInfo().onSuccess { user -> _state.update { it.copy(userInfo = user) } }
        }
    }

    fun onScan(qrValue: String) {                                   // ← onScan (was 'scan')
        if (qrValue.isBlank()) return
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        scope.launch {
            getExaminee(qrValue)
                .onSuccess { examinee ->
                    _state.update { it.copy(isLoading = false, examinee = examinee) }
                }
                .onFailure { error ->
                    val expired = error is DomainError.SessionExpired
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Something went wrong",
                            sessionExpired = expired,
                        )
                    }
                }
        }
    }

    fun clearResult() { _state.update { it.copy(examinee = null, errorMessage = null) } }

    fun logout() {                                                  // ← no args (was logout(onDone))
        scope.launch {
            logoutUseCase()
            _state.update { it.copy(loggedOut = true) }
        }
    }
}