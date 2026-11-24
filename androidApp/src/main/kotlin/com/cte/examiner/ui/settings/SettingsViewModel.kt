package com.cte.examiner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cte.examiner.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val container: AppContainer) : ViewModel() {

    val baseUrl: StateFlow<String> = container.getBaseUrl()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun changeBaseUrl(url: String) {
        viewModelScope.launch { container.setBaseUrl(url) }
    }
}