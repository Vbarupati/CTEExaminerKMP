package com.cte.examiner.ui.home

import android.content.Context
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

fun scanBarcode(
    context: Context,
    onResult: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_EAN_13
        )
        .enableAutoZoom()
        .build()

    val scanner = GmsBarcodeScanning.getClient(context, options)
    val moduleInstall = ModuleInstall.getClient(context)

    // 1) Check if the scanner module is already installed
    moduleInstall.areModulesAvailable(scanner)
        .addOnSuccessListener { response ->
            if (response.areModulesAvailable()) {
                println("SCAN module ready -> scanning")
                startScan(scanner, onResult, onError)
            } else {
                // 2) Not installed -> install it, then scan
                println("SCAN module not ready -> installing")
                val request = ModuleInstallRequest.newBuilder()
                    .addApi(scanner)
                    .build()
                moduleInstall.installModules(request)
                    .addOnSuccessListener {
                        println("SCAN module installed -> scanning")
                        startScan(scanner, onResult, onError)
                    }
                    .addOnFailureListener { e ->
                        println("SCAN module install failed: ${e.message}")
                        onError("Scanner is downloading, please try again in a moment.")
                    }
            }
        }
        .addOnFailureListener {
            // Couldn't check availability — just try scanning
            startScan(scanner, onResult, onError)
        }
}

private fun startScan(
    scanner: GmsBarcodeScanner,
    onResult: (String) -> Unit,
    onError: (String) -> Unit
) {
    scanner.startScan()
        .addOnSuccessListener { barcode ->
            println("SCAN success: ${barcode.rawValue}")
            barcode.rawValue?.let { onResult(it) }
        }
        .addOnCanceledListener {
            println("SCAN canceled")
        }
        .addOnFailureListener { e ->
            println("SCAN failed: ${e.message}")
            onError(e.message ?: "Scan failed")
        }
}