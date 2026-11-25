package com.cte.examiner.data.remote

import com.cte.examiner.getPlatform

object NetworkConstants {

    /** Office network server (Android phone on office Wi-Fi). */
    const val TEST_BASE_URL = "http://172.23.114.3/sites/cte/"

    /** Public server (iOS simulator / Appetize — can't reach the office IP). */
    const val RELEASE_BASE_URL = "https://vjdemo.trakaid.com/sites/cte/"

    /** True when running on iOS (getPlatform().name = "iOS 18.2" …). */
    private val isIos: Boolean = getPlatform().name.startsWith("iOS", ignoreCase = true)

    /** Android → office IP, iOS → public server. */
    const val API_VERSION = "1.0"
    val DEFAULT_BASE_URL: String = if (isIos) RELEASE_BASE_URL else TEST_BASE_URL

    // Timeouts
    const val CONNECT_TIMEOUT_MS = 30_000L
    const val REQUEST_TIMEOUT_MS = 30_000L
    const val SOCKET_TIMEOUT_MS  = 30_000L
    const val ENABLE_LOGGING = true

    // Headers
    const val HEADER_AUTHORIZATION = "Authorization"
    const val BEARER_PREFIX = "Bearer "
}
