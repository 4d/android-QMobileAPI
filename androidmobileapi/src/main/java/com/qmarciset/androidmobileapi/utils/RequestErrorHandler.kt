package com.qmarciset.androidmobileapi.utils

import com.google.gson.Gson
import com.qmarciset.androidmobileapi.model.error.ErrorResponse
import okhttp3.ResponseBody
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

    fun tryToParseError(response: okhttp3.Response): ErrorResponse? {
        // If buffer is read here, it won't be readable later to decode the response.
        // Therefore, we use peekBody() to copy the buffer instead of body()
        val copyResponse = response.peekBody(Long.MAX_VALUE)
        // val responseBody = response.body
        val json = copyResponse.string()
        return Gson().parseJsonToType<ErrorResponse>(json)
    }

    fun tryToParseError2(response: Response<ResponseBody>): ErrorResponse? {
        val responseBody = response.body()
        val json = responseBody?.string()
        return Gson().parseJsonToType<ErrorResponse>(json)
    }
}
