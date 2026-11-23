package com.cte.examiner.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

class AuthConfig {
    var tokenProvider: suspend () -> String? = { null }
    var sessionHeaders: suspend () -> Map<String, String> = { emptyMap() }
}

private val AuthHeadersPlugin = createClientPlugin("AuthHeaders", ::AuthConfig) {
    val tokenProvider = pluginConfig.tokenProvider
    val sessionHeaders = pluginConfig.sessionHeaders
    onRequest { request, _ ->
        tokenProvider()?.takeIf { it.isNotBlank() }?.let { token ->
            request.headers.append(
                NetworkConstants.HEADER_AUTHORIZATION,
                NetworkConstants.BEARER_PREFIX + token
            )
        }
        sessionHeaders().forEach { (name, value) ->
            if (value.isNotBlank()) request.headers.append(name, value)
        }
    }
}

fun createHttpClient(
    getToken: suspend () -> String?,
    getHeaders: suspend () -> Map<String, String>,
): HttpClient = HttpClient {                     // ← no engine: Ktor picks it from the classpath
    expectSuccess = false

    install(ContentNegotiation) { json(AppJson) }

    install(HttpTimeout) {
        connectTimeoutMillis = NetworkConstants.CONNECT_TIMEOUT_MS
        requestTimeoutMillis = NetworkConstants.REQUEST_TIMEOUT_MS
        socketTimeoutMillis  = NetworkConstants.SOCKET_TIMEOUT_MS
    }

    if (NetworkConstants.ENABLE_LOGGING) {
        install(Logging) { level = LogLevel.ALL }
    }

    install(AuthHeadersPlugin) {
        tokenProvider = getToken
        sessionHeaders = getHeaders
    }
}