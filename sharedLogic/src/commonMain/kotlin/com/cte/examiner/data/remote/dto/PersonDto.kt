package com.cte.examiner.data.remote.dto

import com.cte.examiner.domain.model.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    @SerialName("UserName")           val userName: String? = null,
    @SerialName("FullName")           val fullName: String? = null,
    @SerialName("Email")              val email: String? = null,
    @SerialName("UserId")             val userId: String? = null,
    @SerialName("UserProfileId")      val userProfileId: String? = null,
    @SerialName("Language")           val language: String? = null,
    @SerialName("SubsNum")            val subsNum: String? = null,
    @SerialName("CompanyId")          val companyId: String? = null,
    @SerialName("CompanyName")        val companyName: String? = null,
    @SerialName("LocationId")         val locationId: String? = null,
    @SerialName("LocationName")       val locationName: String? = null,
    @SerialName("Role")               val role: String? = null,
    @SerialName("PasswordExpiryDays") val passwordExpiryDays: Int? = null,
    @SerialName("SessionExpiresAt")   val sessionExpiresAt: String? = null,
    @SerialName("HasAssetTracking")   val hasAssetTracking: Boolean = false,
    @SerialName("HasCustomerModule")  val hasCustomerModule: Boolean = false,
    @SerialName("HasOfflineModule")   val hasOfflineModule: Boolean = false,
)

fun PersonDto.toDomain() = UserInfo(
    userName = userName.orEmpty(),
    fullName = fullName.orEmpty(),
    email = email.orEmpty(),
    userId = userId.orEmpty(),
    userProfileId = userProfileId.orEmpty(),
    language = language ?: "en",
    subsNum = subsNum.orEmpty(),
    companyId = companyId.orEmpty(),
    companyName = companyName.orEmpty(),
    locationId = locationId.orEmpty(),
    locationName = locationName.orEmpty(),
    role = role.orEmpty(),
    passwordExpiryDays = passwordExpiryDays ?: 0,
    sessionExpiresAt = sessionExpiresAt.orEmpty(),
    hasAssetTracking = hasAssetTracking,
    hasCustomerModule = hasCustomerModule,
    hasOfflineModule = hasOfflineModule,
)