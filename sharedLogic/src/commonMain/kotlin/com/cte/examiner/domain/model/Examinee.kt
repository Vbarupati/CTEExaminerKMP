package com.cte.examiner.domain.model

data class Examinee(
    val ownerANum: String? = null,
    val ownerName: String? = null,
    val product: String? = null,
    val filledAt: String? = null,
    val batchNumber: String? = null,
    val batchDate: String? = null,
    val expiryDate: String? = null,
    val status: String = "",          // non-null, defaults to ""
    val isAllowed: Boolean = false,
    val assetQualityCode: String = "" // non-null, carries the scanned code
)