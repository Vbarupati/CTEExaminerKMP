package com.cte.examiner.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CQualityBatchNumberDTO(
    @SerialName("OwnerANum")        val ownerANum: String? = null,
    @SerialName("OwnerName")        val ownerName: String? = null,
    @SerialName("Product")          val product: String? = null,
    @SerialName("FilledAt")         val filledAt: String? = null,
    @SerialName("FillBatchNum")     val fillBatchNum: String? = null,
    @SerialName("FillBatchDate")    val fillBatchDate: String? = null,
    @SerialName("ExpiryDate")       val expiryDate: String? = null,
    @SerialName("Status")           val status: String? = null,
    @SerialName("AssetQualityCode") val assetQualityCode: String? = null,
)