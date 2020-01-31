package com.qmarciset.androidmobileapi.auth

import android.util.Patterns

/**
 * Verifies that the current String is a valid mail address
 */
fun String.isEmailValid(): Boolean = Patterns.EMAIL_ADDRESS.toRegex().matches(this)
