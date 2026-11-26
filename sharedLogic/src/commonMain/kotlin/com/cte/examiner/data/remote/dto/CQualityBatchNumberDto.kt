package com.cte.examiner.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CQualityBatchNumberDTO(
    @SerialName("SubsNum")          val subsNum: String? = null,
    @SerialName("OwnerANum")        val ownerANum: String? = null,
    @SerialName("OwnerName")        val ownerName: String? = null,
    @SerialName("Product")          val product: String? = null,
    @SerialName("FillCmpLoc")       val fillCmpLoc: String? = null,      // → "Filled at"
    @SerialName("FillBatchNum")     val fillBatchNum: String? = null,    // → "Batch number"
    @SerialName("BatchStartDtTm")   val batchStartDtTm: String? = null,  // → "Batch date"
    @SerialName("BatchEndDtTm")     val batchEndDtTm: String? = null,    // → "Expiry date"
    @SerialName("Status")           val status: String? = null,
    @SerialName("AssetQualityCode") val assetQualityCode: String? = null,
    @SerialName("Owner_Tid")        val ownerTid: Int? = null,
)