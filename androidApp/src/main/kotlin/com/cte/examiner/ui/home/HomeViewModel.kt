package com.cte.examiner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cte.examiner.di.AppContainer
import com.cte.examiner.presentation.home.HomeStore

class HomeViewModel(container: AppContainer) : ViewModel() {
    val store = HomeStore(
        scope = viewModelScope,
        getUserInfo = container.getUserInfo,
        getExaminee = container.getExaminee,
        logoutUseCase = container.logoutUseCase
    )
    // no init block needed — HomeStore loads the profile itself
}
