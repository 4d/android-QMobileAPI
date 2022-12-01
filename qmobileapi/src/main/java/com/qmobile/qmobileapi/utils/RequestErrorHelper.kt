/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.JsonSyntaxException
import com.qmobile.qmobileapi.model.error.ErrorResponse
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection

/**
 * Helper class for API errors
 */
object RequestErrorHelper {

    /**
     * Parses error from response
     */
    fun okhttp3.Response.tryToParseError(mapper: ObjectMapper): ErrorResponse? {
        // If buffer is read here, it won't be readable later to decode the response.
        // Therefore, we use peekBody() to copy the buffer instead of body()
        val copyResponse = this.peekBody(Long.MAX_VALUE)
        // val responseBody = response.body
        val json = copyResponse.string()
        return toErrorResponse(json, mapper)
    }

    fun toErrorResponse(jsonString: String?, mapper: ObjectMapper): ErrorResponse? {
        if (jsonString == null) return null
        return try {
            mapper.parseToType(jsonString)
        } catch (e: JsonSyntaxException) {
            Timber.d(e.message.orEmpty())
            null
        }
    }

    fun Response<ResponseBody>?.isUnauthorized(): Boolean = this?.code() == HttpURLConnection.HTTP_UNAUTHORIZED
}
