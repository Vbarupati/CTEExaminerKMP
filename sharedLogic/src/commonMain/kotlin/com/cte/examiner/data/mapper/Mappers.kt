package com.cte.examiner.data.mapper

import com.cte.examiner.data.remote.dto.CQualityBatchNumberDTO
import com.cte.examiner.data.remote.findBool
import com.cte.examiner.data.remote.findInt
import com.cte.examiner.data.remote.findString
import com.cte.examiner.domain.model.Examinee
import com.cte.examiner.domain.model.UserInfo
import kotlinx.serialization.json.JsonObject

// ---------------- Examinee ----------------

fun CQualityBatchNumberDTO.toDomain(): Examinee {
    val rawStatus = status.orEmpty()
    return Examinee(
        ownerANum = ownerANum?.takeIf { it.isNotBlank() },
        ownerName = ownerName?.takeIf { it.isNotBlank() },
        product = product?.takeIf { it.isNotBlank() },
        filledAt = filledAt?.takeIf { it.isNotBlank() },
        batchNumber = fillBatchNum?.takeIf { it.isNotBlank() },
        batchDate = fillBatchDate.dateOnly(),
        expiryDate = expiryDate.dateOnly(),
        status = rawStatus.cleanStatus(),
        isAllowed = !rawStatus.isErrorStatus(),   // genuine unless status is an error
        assetQualityCode = assetQualityCode.orEmpty(),
        found = true                               // ← came from a real 200 record
    )
}

/** "E:..." prefix marks an error/blocked status from the server. */
private fun String.isErrorStatus(): Boolean = trim().startsWith("E:", ignoreCase = true)

/** Strip the "E:" marker for display. */
private fun String.cleanStatus(): String =
    trim().removePrefix("E:").removePrefix("e:").trim()

/** Keep only the date part of an ISO timestamp ("2026-11-23T10:00:00" → "2026-11-23"). */
private fun String?.dateOnly(): String? {
    val v = this?.trim().orEmpty()
    if (v.isEmpty()) return null
    return v.substringBefore('T').substringBefore(' ').takeIf { it.isNotBlank() }
}

// ---------------- UserInfo ----------------

fun JsonObject.toUserInfo(): UserInfo = UserInfo(
    userName = findString("userName", "UserName", "login").orEmpty(),
    fullName = findString("fullName", "FullName", "name").orEmpty(),
    email = findString("email", "Email").orEmpty(),
    userId = findString("userId", "UserId").orEmpty(),
    userProfileId = findString("userProfileId", "UserProfileId").orEmpty(),
    language = findString("language", "Language") ?: "en",
    subsNum = findString("subsNum", "SubsNum").orEmpty(),
    companyId = findString("companyId", "CompanyId").orEmpty(),
    companyName = findString("companyName", "CompanyName").orEmpty(),
    locationId = findString("locationId", "LocationId").orEmpty(),
    locationName = findString("locationName", "LocationName").orEmpty(),
    role = findString("role", "Role").orEmpty(),
    passwordExpiryDays = findInt("passwordExpiryDays", "PasswordExpiryDays") ?: 0,
    sessionExpiresAt = findString("sessionExpiresAt", "SessionExpiresAt").orEmpty(),
    hasAssetTracking = findBool("hasAssetTracking", "HasAssetTracking") ?: false,
    hasCustomerModule = findBool("hasCustomerModule", "HasCustomerModule") ?: false,
    hasOfflineModule = findBool("hasOfflineModule", "HasOfflineModule") ?: false
)