package com.qmarciset.androidmobileapi.utils

import retrofit2.Response
import timber.log.Timber

object RequestErrorHandler {

    fun handleError(error: Any?) {
        Timber.e("Error: $error")
        when (error) {
            is Response<*> -> {
            }
            is Throwable -> {
            }
        }
    }
}
