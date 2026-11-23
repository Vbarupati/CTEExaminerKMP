package com.cte.examiner.data.remote

import com.cte.examiner.domain.error.DomainError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Runs [block] and wraps the outcome in kotlin [Result].
 * A thrown [DomainError] passes through unchanged; everything else
 * is mapped to the closest DomainError.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e                                   // never swallow coroutine cancellation
    } catch (e: DomainError) {
        Result.failure(e)                         // already a domain error
    } catch (e: ClientRequestException) {
        val code = e.response.status.value
        val error = if (e.response.status == HttpStatusCode.Unauthorized) {
            DomainError.Unauthorized
        } else {
            DomainError.Http(code, runCatching { e.response.bodyAsText() }.getOrDefault(""))
        }
        Result.failure(error)
    } catch (e: ServerResponseException) {
        Result.failure(DomainError.Http(e.response.status.value, ""))
    } catch (e: HttpRequestTimeoutException) {
        Result.failure(DomainError.Timeout)
    } catch (e: IOException) {
        Result.failure(DomainError.Network)
    } catch (e: SerializationException) {
        Result.failure(DomainError.Serialization)
    } catch (e: Throwable) {
        Result.failure(DomainError.Unknown(e.message ?: "Unexpected error"))
    }
}