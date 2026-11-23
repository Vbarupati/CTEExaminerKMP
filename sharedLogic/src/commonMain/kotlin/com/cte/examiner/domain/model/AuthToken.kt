package com.cte.examiner.domain.model

data class AuthToken(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresAt: String? = null
)