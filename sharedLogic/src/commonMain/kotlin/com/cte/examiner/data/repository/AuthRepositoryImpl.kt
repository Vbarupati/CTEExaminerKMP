package com.cte.examiner.data.repository

import com.cte.examiner.data.local.SettingsStore
import com.cte.examiner.data.remote.ApiPaths
import com.cte.examiner.data.remote.CteApi
import com.cte.examiner.data.remote.LoginParams
import com.cte.examiner.data.remote.findString
import com.cte.examiner.data.remote.safeApiCall
import com.cte.examiner.data.remote.toJsonObjectOrNull
import com.cte.examiner.data.remote.urlFor
import com.cte.examiner.domain.error.DomainError
import com.cte.examiner.domain.repository.AuthRepository
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val api: CteApi,
    private val settings: SettingsStore
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Unit> =
        safeApiCall {
            val url = settings.urlFor(ApiPaths.LOGIN)

            val response = api.login(
                url = url,
                grantType = LoginParams.GRANT_TYPE,
                username = username,
                password = password,
                userType = LoginParams.USER_TYPE,
                deviceType = LoginParams.DEVICE_TYPE,   // "Android" / "iOS" via platformDeviceType
                rDeviceTid = LoginParams.R_DEVICE_TID,
                networkIP = "",
                ipCountry = LoginParams.IP_COUNTRY,
                accessType = LoginParams.ACCESS_TYPE
            )

            val bodyText = response.bodyAsText()
            val json = bodyText.toJsonObjectOrNull() ?: throw DomainError.Serialization

            // Token can come back under a few possible keys — findString is case-insensitive.
            val token = json.findString("access_token", "token", "accessToken")
            if (token.isNullOrBlank()) throw DomainError.Unauthorized

            val examiner = json.findString("userName", "fullName", "name") ?: username

            settings.saveSession(
                token = token,
                examinerName = examiner,
                sessionJson = bodyText
            )
            Unit
        }

    override suspend fun logout(): Result<Unit> = safeApiCall {
        settings.clearSession()
        Unit
    }

    override fun isLoggedIn(): Flow<Boolean> =
        settings.token.map { !it.isNullOrBlank() }    // isNullOrBlank IS allowed on String?}
}