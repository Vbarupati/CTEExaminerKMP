package com.cte.examiner.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters

class CteApi(private val client: HttpClient) {

    /** POST {server}/api/authuser/login  (form fields) → raw JSON (read via response.body()) */
    suspend fun login(
        url: String,
        grantType: String?,
        username: String?,
        password: String?,
        userType: Int?,
        deviceType: String?,
        rDeviceTid: Int?,
        networkIP: String?,
        ipCountry: String?,
        accessType: Int?,
    ): HttpResponse = client.submitForm(
        url = url,
        formParameters = Parameters.build {
            grantType?.let  { append("grant_type", it) }
            username?.let   { append("Username", it) }
            password?.let   { append("password", it) }
            userType?.let   { append("userType", it.toString()) }
            deviceType?.let { append("DeviceType", it) }
            rDeviceTid?.let { append("RDeviceTid", it.toString()) }
            networkIP?.let  { append("networkIP", it) }
            ipCountry?.let  { append("IPCountry", it) }
            accessType?.let { append("AccessType", it.toString()) }
        }
    )

    /** GET {server}/api/user/person/{userId}  — token added by the client (DefaultRequest) */
    suspend fun getPerson(url: String): HttpResponse =
        client.get(url)

    /** GET {server}/api/printer/cqbatchInfo?qcode={quid}  — token added by the client */
    suspend fun getExaminee(url: String, qcode: String): HttpResponse =
        client.get(url) {
            url { parameters.append("qcode", qcode) }
        }
}