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
fun String.isRemoteUrlValid(): Boolean = Patterns.WEB_URL.toRegex().matches(this)
