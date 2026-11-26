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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
                deviceType = LoginParams.DEVICE_TYPE,
                rDeviceTid = LoginParams.R_DEVICE_TID,
                networkIP = "",
                ipCountry = LoginParams.IP_COUNTRY,
                accessType = LoginParams.ACCESS_TYPE
            )

            val bodyText = response.bodyAsText()
            val json = bodyText.toJsonObjectOrNull() ?: throw DomainError.Serialization

            val token = json.findString("access_token", "token", "accessToken")
            if (token.isNullOrBlank()) throw DomainError.Unauthorized

            val examiner = json.findString("userName", "fullName", "name") ?: username

            // 1) Save first so the token is attached to the getPerson call
            settings.saveSession(token = token, examinerName = examiner, sessionJson = bodyText)

            // 2) Fetch person (SubsNum, userProfileId…) and merge into the stored session
            val person = fetchPerson(json)
            if (person != null) {
                val merged = buildJsonObject {
                    json.forEach { (k, v) -> put(k, v) }
                    put("person", person)
                }
                settings.saveSession(token = token, examinerName = examiner, sessionJson = merged.toString())
            }
            Unit
        }

    /** GET api/user/person/{id}; tries userId then userProfileId. Needs the saved token. */
    private suspend fun fetchPerson(login: JsonObject): JsonObject? {
        val ids = listOfNotNull(login.findString("userId"), login.findString("userProfileId")).distinct()
        for (id in ids) {
            val resUrl = settings.urlFor(ApiPaths.PERSON + id)
            val res = runCatching { api.getPerson(resUrl) }.getOrNull() ?: continue
            if (res.status.isSuccess()) {
                res.bodyAsText().toJsonObjectOrNull()?.let { return it }
            }
        }
        return null
    }

    override suspend fun logout(): Result<Unit> = safeApiCall {
        settings.clearSession()
        Unit
    }

    override fun isLoggedIn(): Flow<Boolean> =
        settings.token.map { !it.isNullOrBlank() }
}