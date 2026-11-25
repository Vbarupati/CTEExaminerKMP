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
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val api: CteApi,
    private val settings: SettingsStore
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Unit> =
        safeApiCall {
            val networkIp = fetchNetworkIp()                       // ← was missing
            println("LOGIN networkIP = '$networkIp'")

            val url = settings.urlFor(ApiPaths.LOGIN)
            val response = api.login(
                url = url,
                grantType = LoginParams.GRANT_TYPE,
                username = username,
                password = password,
                userType = LoginParams.USER_TYPE,
                deviceType = LoginParams.DEVICE_TYPE,
                rDeviceTid = LoginParams.R_DEVICE_TID,
                networkIP = networkIp,                              // ← real IP now
                ipCountry = LoginParams.IP_COUNTRY,
                accessType = LoginParams.ACCESS_TYPE
            )

            val bodyText = response.bodyAsText()

            // Surface the REAL server error (no more fake "session expired")
            if (!response.status.isSuccess()) {
                val msgCode = response.headers["msgcode"]
                val detail = if (msgCode != null) "[$msgCode] $bodyText" else bodyText
                println("LOGIN server error ${response.status.value}: $detail")
                throw DomainError.Http(response.status.value, detail)
            }

            val json = bodyText.toJsonObjectOrNull() ?: throw DomainError.Serialization
            val token = json.findString("access_token", "token", "accessToken")
            if (token.isNullOrBlank()) throw DomainError.Unauthorized

            val examiner = json.findString("userName", "fullName", "name") ?: username
            settings.saveSession(token = token, examinerName = examiner, sessionJson = bodyText)
            Unit
        }

    override suspend fun logout(): Result<Unit> = safeApiCall {
        settings.clearSession()
        Unit
    }

    override fun isLoggedIn(): Flow<Boolean> =
        settings.token.map { it.isNotBlank() }

    /** Public IP for the login's networkIP field (like the original app's jsonip.com call). */
    private suspend fun fetchNetworkIp(): String = runCatching {
        val resp = api.getPublicIp()
        resp.bodyAsText().toJsonObjectOrNull()?.findString("ip").orEmpty()
    }.getOrDefault("")
}
