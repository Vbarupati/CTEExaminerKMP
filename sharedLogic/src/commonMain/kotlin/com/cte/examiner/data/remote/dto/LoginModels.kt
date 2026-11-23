package com.cte.examiner.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Convenience holder for the login form fields (optional). */
data class LoginRequest(
    val username: String,
    val password: String,
)

/**
 * Typed login response — OPTIONAL.
 * AuthRepositoryImpl currently parses the raw JsonObject with findString() because
 * the server's field casing varies. Use this only if your server returns a stable shape.
 */
@Serializable
data class LoginResponse(
    @SerialName("access_token")  val accessToken: String? = null,
    @SerialName("token_type")    val tokenType: String? = null,
    @SerialName("expires_in")    val expiresIn: Long? = null,
    @SerialName("userProfileId") val userProfileId: String? = null,
    @SerialName("subsNum")       val subsNum: String? = null,
    @SerialName("fullName")      val fullName: String? = null,
    @SerialName("userName")      val userName: String? = null,
)