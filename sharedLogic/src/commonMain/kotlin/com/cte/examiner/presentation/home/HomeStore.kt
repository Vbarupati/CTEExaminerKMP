package com.cte.examiner.presentation.home

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
    private val logoutUseCase: LogoutUseCase
) {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun loadUserInfo() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        scope.launch {
            getUserInfo()
                .onSuccess { info ->
                    _state.update { it.copy(isLoading = false, userInfo = info) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Could not load user")
                    }
                }
        }
    }

    /** Called with a scanned QR/barcode value (raw or full URL). */
    fun onScan(scannedValue: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null, examinee = null) }
        scope.launch {
            getExaminee(scannedValue)
                .onSuccess { examinee ->
                    _state.update { it.copy(isLoading = false, examinee = examinee) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Lookup failed")
                    }
                }
        }
    }

    fun logout() {
        scope.launch {
            logoutUseCase()
            _state.update { it.copy(loggedOut = true) }
        }
    }

    fun consumeError() {
        _state.update { it.copy(errorMessage = null) }
    }
    fun clearResult() {
        _state.update { it.copy(examinee = null, errorMessage = null) }
    }
}