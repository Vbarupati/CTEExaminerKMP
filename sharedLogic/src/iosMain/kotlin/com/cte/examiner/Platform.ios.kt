package com.cte.examiner

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual val platformDeviceType: String = "iOS"

actual fun currentDeviceInfo(): DeviceInfo {
    val device = UIDevice.currentDevice
    return DeviceInfo(
        deviceId = device.identifierForVendor?.UUIDString ?: "",
        manufacturer = "Apple",
        model = device.model,
        modelIdentifier = device.model,
        systemName = device.systemName,
        systemVersion = device.systemVersion
    )
}

actual fun currentLocalTime(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    return formatter.stringFromDate(NSDate())
}