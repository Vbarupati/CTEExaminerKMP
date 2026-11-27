package com.cte.examiner.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json

private class SessionAuthConfig {
    var getToken: (suspend () -> String?)? = null
    var getHeaders: (suspend () -> Map<String, String>)? = null
}

private val SessionAuthPlugin = createClientPlugin("SessionAuthPlugin", ::SessionAuthConfig) {
    val getToken = pluginConfig.getToken
    val getHeaders = pluginConfig.getHeaders
    onRequest { request, _ ->                       // ← suspend context: .first() / buildSessionHeaders() allowed
        getToken?.invoke()?.takeIf { it.isNotBlank() }?.let { token ->
            request.headers.append(HttpHeaders.Authorization, NetworkConstants.BEARER_PREFIX + token)
        }
        getHeaders?.invoke()?.forEach { (k, v) ->
            request.headers.append(k, v)
        }
    }
}

fun createHttpClient(
    getToken: suspend () -> String?,
    getHeaders: suspend () -> Map<String, String>,
): HttpClient = HttpClient {
    expectSuccess = false

    install(ContentNegotiation) { json(AppJson) }

    install(HttpTimeout) {
        connectTimeoutMillis = NetworkConstants.CONNECT_TIMEOUT_MS
        requestTimeoutMillis = NetworkConstants.REQUEST_TIMEOUT_MS
        socketTimeoutMillis  = NetworkConstants.SOCKET_TIMEOUT_MS
    }

    if (NetworkConstants.ENABLE_LOGGING) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) { println("CTE_HTTP: $message") }
            }
            level = LogLevel.ALL
        }
    }

    install(SessionAuthPlugin) {
        this.getToken = getToken
        this.getHeaders = getHeaders
    }
}