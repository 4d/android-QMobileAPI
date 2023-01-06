/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.network

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FeedbackApiService {

    /*
     * Checks server accessibility
     */
    @GET("/")
    fun checkAccessibility(): Single<Response<ResponseBody>>

    /**
     * Send feedback and crash logs
     */
    @Multipart
    @POST("/")
    fun sendFeedbackAndLogs(
        @Header("Content-Type") mime: String = "application/zip",
        @Part body: RequestBody,
        @Part file: MultipartBody.Part?
    ): Single<Response<ResponseBody>>
}
