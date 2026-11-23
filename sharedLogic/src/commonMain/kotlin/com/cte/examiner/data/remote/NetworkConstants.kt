package com.cte.examiner.data.remote

object NetworkConstants {

    /** Server used on first launch, before the user changes it in Settings. */
    const val DEFAULT_BASE_URL = "http://172.23.114.3/sites/cte/"

    /** Ktor client timeouts (milliseconds). */
    const val CONNECT_TIMEOUT_MS = 30_000L
    const val REQUEST_TIMEOUT_MS = 30_000L
    const val SOCKET_TIMEOUT_MS  = 30_000L

    /** Request/response logging (turn off for release). */
    const val ENABLE_LOGGING = true

    /** Content types. */
    const val CONTENT_TYPE_JSON = "application/json"
    const val CONTENT_TYPE_FORM = "application/x-www-form-urlencoded"

    /** Common header names / prefixes. */
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE  = "Content-Type"
    const val BEARER_PREFIX = "Bearer "
}