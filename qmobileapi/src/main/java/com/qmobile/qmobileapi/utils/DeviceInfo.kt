/*
 * Created by qmarciset on 13/7/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import org.json.JSONObject
import timber.log.Timber

object DeviceInfo {

    private const val ID = "id"
    const val SIMULATOR = "simulator"
    private const val DESCRIPTION = "description"
    private const val VERSION = "version"
    private const val OS = "os"

    fun build(context: Context) = JSONObject().apply {
        val isEmulator = isEmulator()
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        put(ID, androidId)
        put(SIMULATOR, isEmulator) // false
        put(DESCRIPTION, if (isEmulator) "Simulator" else Build.MODEL) // SM-G950F
        put(VERSION, "${Build.VERSION.SDK_INT} (${getVersionName()})") // 28 (Android P)
        put(OS, "Android")
    }

    @Suppress("ComplexMethod")
    private fun isEmulator(): Boolean = Build.FINGERPRINT.startsWith("generic") ||
        Build.FINGERPRINT.startsWith("unknown") ||
        Build.MODEL.contains("google_sdk") ||
        Build.MODEL.contains("Emulator") ||
        Build.MODEL.contains("Android SDK built for x86") ||
        Build.HARDWARE.contains("ranchu") ||
        Build.HARDWARE.contains("goldfish") ||
        Build.PRODUCT.contains("google_sdk") ||
        Build.PRODUCT.contains("vbox86p") ||
        Build.PRODUCT.contains("emulator") ||
        Build.PRODUCT.contains("simulator") ||
        Build.PRODUCT.contains("sdk_x86") ||
        Build.PRODUCT.contains("sdk_google") ||
        Build.BOARD == "QC_Reference_Phone" || // bluestacks
        Build.MANUFACTURER.contains("Genymotion") ||
        Build.HOST.startsWith("Build") || // MSI App Player
        (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))

    private fun getVersionName(): String {
        val fields = Build.VERSION_CODES::class.java.fields
        var codeName = "UNKNOWN"
        fields.filter {
            var buildVersion = -1
            try {
                buildVersion = it.getInt(Build.VERSION_CODES::class)
            } catch (e: IllegalArgumentException) {
                Timber.d("Couldn't get Build.VERSION_CODES : ${e.message}")
            }
            buildVersion == Build.VERSION.SDK_INT
        }
            .forEach { codeName = it.name }
        return "Android $codeName"
    }
}
