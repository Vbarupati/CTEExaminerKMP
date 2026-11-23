package com.cte.examiner.domain.error

/**
 * All failures surfaced to the UI as a single type.
 * Extends Exception so it works with Result.failure(...) and safeApiCall.
 * `message` is safe to show the user directly.
 */
sealed class DomainError(override val message: String) : Exception(message) {

    /** No internet / DNS / socket failure. */
    data object Network : DomainError("No internet connection. Please check your network.")

    /** Request took too long. */
    data object Timeout : DomainError("The request timed out. Please try again.")

    /** 401 — token missing/expired. */
    data object Unauthorized : DomainError("Your session has expired. Please log in again.")

    /** Response body couldn't be parsed. */
    data object Serialization : DomainError("Unexpected response from the server.")

    /** 204 / empty — asset not found (shown as a normal state, not a crash). */
    data object NoRecord : DomainError("No record found for this code.")

    /** Any other HTTP error, with the status code (and optional body for logs). */
    data class Http(val code: Int, val body: String? = null) :
        DomainError("Server error ($code).")

    /** Anything unclassified. */
    data class Unknown(val detail: String? = null) :
        DomainError(detail ?: "Something went wrong. Please try again.")
}