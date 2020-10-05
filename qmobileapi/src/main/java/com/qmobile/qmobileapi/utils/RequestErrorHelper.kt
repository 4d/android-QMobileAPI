/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.google.gson.Gson
import com.qmobile.qmobileapi.model.error.ErrorResponse
import retrofit2.Response
import timber.log.Timber

/**
 * Helper class for API errors
 */
object RequestErrorHelper {

    fun handleError(error: Any?) {
        Timber.e("Error: $error")
        when (error) {
            is Response<*> -> {
            }
            is Throwable -> {
            }
        }
    }

    /**
     * Parses error from response
     */
    fun tryToParseError(response: okhttp3.Response): ErrorResponse? {
        // If buffer is read here, it won't be readable later to decode the response.
        // Therefore, we use peekBody() to copy the buffer instead of body()
        val copyResponse = response.peekBody(Long.MAX_VALUE)
        // val responseBody = response.body
        val json = copyResponse.string()
        return Gson().parseJsonToType(json)
    }
}
