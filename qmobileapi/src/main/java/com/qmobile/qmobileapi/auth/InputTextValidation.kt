/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.auth

import android.util.Patterns

/**
 * Verifies that the current String is a valid mail address
 */
fun String.isEmailValid(): Boolean = Patterns.EMAIL_ADDRESS.toRegex().matches(this)

/**
 * Verifies that the current String is a server address
 */
fun String.isUrlValid(): Boolean = Patterns.WEB_URL.toRegex().matches(this)

/**
 * Verifies that the current String is a valid port
 */
fun String.isPortValid(): Boolean =
    "^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])\$".toRegex().matches(this)
