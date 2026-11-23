package com.cte.examiner

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/** "Android" or "iOS" — sent to the API as deviceType. */
expect val platformDeviceType: String

data class DeviceInfo(
    val deviceId: String,
    val manufacturer: String,
    val model: String,
    val modelIdentifier: String,
    val systemName: String,
    val systemVersion: String
)

expect fun currentDeviceInfo(): DeviceInfo

/** Local time as "yyyy-MM-dd HH:mm:ss" — used in login/network params. */
expect fun currentLocalTime(): String