package com.cte.examiner.presentation.home

import com.cte.examiner.domain.model.Examinee
import com.cte.examiner.domain.model.UserInfo

data class HomeState(
    val userInfo: UserInfo? = null,
    val examinee: Examinee? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedOut: Boolean = false,
    val sessionExpired: Boolean = false,
)