package com.cte.examiner.domain.error

sealed class DomainError(message: String) : Exception(message) {
    data object Network : DomainError("Network error")
    data object Timeout : DomainError("Request timed out")
    data object Unauthorized : DomainError("Unauthorized")
    data object SessionExpired : DomainError("Session expired")
    data object Serialization : DomainError("Could not read server response")
    data object NoRecord : DomainError("No record found")
    data class Http(val code: Int, val body: String) : DomainError("HTTP $code")
    data class Unknown(val detail: String) : DomainError(detail)
}