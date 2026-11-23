package com.cte.examiner.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject

/** Shared JSON config — lenient so odd server responses still parse. */
val AppJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

/** Parse a raw JSON string into a JsonObject, or null if it isn't valid. */
fun String.toJsonObjectOrNull(): JsonObject? =
    runCatching { AppJson.parseToJsonElement(this).jsonObject }.getOrNull()

/** Case-insensitive key lookup (server field casing varies). */
private fun JsonObject.getIgnoreCase(key: String): JsonElement? {
    this[key]?.let { return it }
    val lower = key.lowercase()
    for ((k, v) in this) if (k.lowercase() == lower) return v
    return null
}

/** First non-blank string among the given keys (alternates). */
fun JsonObject.findString(vararg keys: String): String? {
    for (key in keys) {
        val prim = getIgnoreCase(key) as? JsonPrimitive ?: continue
        val s = prim.contentOrNull
        if (!s.isNullOrBlank()) return s
    }
    return null
}

/** First parseable Int among the given keys (handles numeric or string). */
fun JsonObject.findInt(vararg keys: String): Int? {
    for (key in keys) {
        val prim = getIgnoreCase(key) as? JsonPrimitive ?: continue
        prim.intOrNull?.let { return it }
        prim.contentOrNull?.trim()?.toIntOrNull()?.let { return it }
    }
    return null
}

/** First parseable Boolean among the given keys (handles true/false/1/0). */
fun JsonObject.findBool(vararg keys: String): Boolean? {
    for (key in keys) {
        val prim = getIgnoreCase(key) as? JsonPrimitive ?: continue
        prim.booleanOrNull?.let { return it }
        when (prim.contentOrNull?.trim()?.lowercase()) {
            "true", "1"  -> return true
            "false", "0" -> return false
        }
    }
    return null
}