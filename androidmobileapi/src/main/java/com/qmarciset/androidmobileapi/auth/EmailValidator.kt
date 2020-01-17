package com.qmarciset.androidmobileapi.auth

import android.util.Patterns

fun String.isEmailValid(): Boolean = Patterns.EMAIL_ADDRESS.toRegex().matches(this)