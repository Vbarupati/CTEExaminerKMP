package com.cte.examiner.domain.model

data class UserInfo(
    val userName: String = "",
    val fullName: String = "",
    val email: String = "",
    val userId: String = "",
    val userProfileId: String = "",
    val language: String = "en",
    val subsNum: String = "",
    val companyId: String = "",
    val companyName: String = "",     // non-null → Text(user.companyName) compiles
    val locationId: String = "",
    val locationName: String = "",
    val role: String = "",
    val passwordExpiryDays: Int = 0,
    val sessionExpiresAt: String = "",
    val hasAssetTracking: Boolean = false,
    val hasCustomerModule: Boolean = false,
    val hasOfflineModule: Boolean = false
)