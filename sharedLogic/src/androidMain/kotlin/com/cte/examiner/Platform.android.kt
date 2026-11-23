package com.cte.examiner

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual val platformDeviceType: String = "Android"

actual fun currentDeviceInfo(): DeviceInfo = DeviceInfo(
    deviceId = Build.ID ?: "",
    manufacturer = Build.MANUFACTURER ?: "",
    model = Build.MODEL ?: "",
    modelIdentifier = Build.DEVICE ?: "",
    systemName = "Android",
    systemVersion = Build.VERSION.RELEASE ?: ""
)

actual fun currentLocalTime(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())