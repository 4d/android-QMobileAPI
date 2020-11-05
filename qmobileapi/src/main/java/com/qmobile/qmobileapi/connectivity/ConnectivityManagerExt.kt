/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.connectivity

import android.net.ConnectivityManager
import android.os.Build

/**
 * Checks if Internet is available
 */
fun ConnectivityManager?.isConnected(
    networkState: NetworkState?
): Boolean = if (sdkNewerThanKitKat) {
    networkState == NetworkState.CONNECTED
} else {
    this.isKitKatConnectedOrConnecting()
}

/**
 * Checks if Internet is available on API < 21
 */
@Suppress("DEPRECATION")
fun ConnectivityManager?.isKitKatConnectedOrConnecting(): Boolean {
    val activeNetworkInfo = this?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

val sdkNewerThanKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
