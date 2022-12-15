/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.network

import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FeedbackApiService {

    /**
     * Send a crash log zip file
     */
    @Multipart
    @POST("/")
    fun sendFile(
        @Header("Content-Type") mime: String = "application/zip",
        @Part zip: RequestBody
    ): Single<Response<ResponseBody>>

    /**
     * Send feedback
     */
    @POST("/")
    fun sendFeedback(@Body body: RequestBody): Single<Response<ResponseBody>>
}
