/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.connectivity

import android.net.ConnectivityManager
import android.os.Build

/**
 * Utility class for network status checking
 */
object NetworkUtils {

    /**
     * Checks if Internet is available
     */
    fun isConnected(
        networkState: NetworkState?,
        connectivityManager: ConnectivityManager?
    ): Boolean = if (sdkNewerThanKitKat) {
        networkState == NetworkState.CONNECTED
    } else {
        isKitKatConnectedOrConnecting(
            connectivityManager
        )
    }

    /**
     * Checks if Internet is available on API < 21
     */
    @Suppress("DEPRECATION")
    private fun isKitKatConnectedOrConnecting(connectivityManager: ConnectivityManager?): Boolean {
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    val sdkNewerThanKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
}
