/*
 * Created by qmarciset on 14/12/2022.
 * 4D SAS
 * Copyright (c) 2022 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object FeedbackApiClient {

    private const val BASE_URL = "https://bugs.4d.com"

    var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null

    @Volatile
    var INSTANCE: FeedbackApiService? = null

    fun getApiService(logBody: Boolean = false): FeedbackApiService {
        INSTANCE?.let {
            return it
        } ?: kotlin.run {
            val service = getClient(logBody).create(FeedbackApiService::class.java)
            INSTANCE = service
            Timber.v("FeedbackApiService created")
            return service
        }
    }

    private fun getClient(logBody: Boolean): Retrofit {
        retrofit?.let {
            return it
        }
        val newRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient ?: initOkHttp(logBody))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        retrofit = newRetrofit
        return newRetrofit
    }

    private fun initOkHttp(logBody: Boolean): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
            .connectTimeout(ApiClient.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(ApiClient.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(ApiClient.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        okHttpClientBuilder.addInterceptor(
            HttpLoggingInterceptor()
                .setLevel(
                    if (logBody) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.BASIC
                    }
                )
        )

        val newOkHttpClient = okHttpClientBuilder.build()
        okHttpClient = newOkHttpClient
        return newOkHttpClient
    }
}
