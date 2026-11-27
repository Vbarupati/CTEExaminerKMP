package com.cte.examiner.data.remote

import com.cte.examiner.data.local.SettingsStore   // ← match SettingsStore's KMM package
import com.cte.examiner.platformDeviceType
import kotlinx.coroutines.flow.first

object ApiPaths {
    const val LOGIN = "api/authuser/login"
    const val PERSON = "api/user/person/"          // + userId
    const val EXAMINEE = "api/printer/cqbatchInfo"
}

/** Fixed values sent with every login. */
object LoginParams {
    const val GRANT_TYPE = "password"
    const val USER_TYPE = 381
    val DEVICE_TYPE: String get() = platformDeviceType  // "Android" / "iOS"
    const val R_DEVICE_TID = 0
    const val IP_COUNTRY = "IN"
    const val ACCESS_TYPE = 517
}

object SessionHeaders {
    val FROM_LOGIN: Map<String, String> = mapOf(
        "UserId" to "userProfileId",
        "SubsNum" to "subsNum",
    )
    val STATIC: Map<String, String> = mapOf(
        "api-version" to "1.0",
    )
}

suspend fun SettingsStore.urlFor(path: String): String =
    baseUrl.first().trimEnd('/') + "/" + path.trimStart('/')
