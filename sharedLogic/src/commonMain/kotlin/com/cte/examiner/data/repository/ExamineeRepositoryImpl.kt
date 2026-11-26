package com.cte.examiner.data.repository

import com.cte.examiner.data.local.SettingsStore
import com.cte.examiner.data.mapper.toDomain
import com.cte.examiner.data.mapper.toUserInfo
import com.cte.examiner.data.remote.ApiPaths
import com.cte.examiner.data.remote.AppJson
import com.cte.examiner.data.remote.CteApi
import com.cte.examiner.data.remote.dto.CQualityBatchNumberDTO
import com.cte.examiner.data.remote.findString
import com.cte.examiner.data.remote.safeApiCall
import com.cte.examiner.data.remote.toJsonObjectOrNull
import com.cte.examiner.data.remote.urlFor
import com.cte.examiner.domain.error.DomainError
import com.cte.examiner.domain.model.Examinee
import com.cte.examiner.domain.model.UserInfo
import com.cte.examiner.domain.repository.ExamineeRepository
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.first

class ExamineeRepositoryImpl(
    private val api: CteApi,
    private val settings: SettingsStore,
) : ExamineeRepository {

    override suspend fun getExaminee(qcode: String): Result<Examinee> = safeApiCall {
        val url = settings.urlFor(ApiPaths.EXAMINEE)
        println("EXAMINEE url: $url?qcode=$qcode")   // ← paste this line from logcat
        val response = api.getExaminee(url, qcode)

        when (response.status) {
            HttpStatusCode.OK -> {
                val raw = response.bodyAsText()
                println("EXAMINEE 200 raw: $raw")          // ← paste this from logcat
                if (raw.isBlank() || raw.trim() == "null") emptyExaminee(qcode)
                else AppJson.decodeFromString<CQualityBatchNumberDTO>(raw).toDomain()
            }
            HttpStatusCode.NoContent -> emptyExaminee(qcode)
            HttpStatusCode.Unauthorized -> throw DomainError.Unauthorized
            else -> throw DomainError.Http(response.status.value, response.bodyAsText())
        }
    }



    override suspend fun getUserInfo(): Result<UserInfo> = safeApiCall {
        // userId comes from the saved login session
        val session = settings.sessionJson.first()?.toJsonObjectOrNull()
            ?: throw DomainError.Unauthorized
        val userId = session.findString("userId", "UserId", "userProfileId")
            ?: throw DomainError.Unauthorized

        val url = settings.urlFor(ApiPaths.PERSON + userId)
        val response = api.getPerson(url)

        if (!response.status.isSuccess()) {
            throw DomainError.Http(response.status.value, response.bodyAsText())
        }
        val json = response.bodyAsText().toJsonObjectOrNull()
            ?: throw DomainError.Serialization

        json.toUserInfo()   // see mapper below
    }

    /** Empty record → carries only the scanned code so the UI shows "Asset Not Found". */
    private fun emptyExaminee(qcode: String) = Examinee(
        ownerANum = null,
        ownerName = null,
        product = null,
        filledAt = null,
        batchNumber = null,
        batchDate = null,
        expiryDate = null,
        status = "No record",
        isAllowed = false,
        assetQualityCode = qcode,
    )
}