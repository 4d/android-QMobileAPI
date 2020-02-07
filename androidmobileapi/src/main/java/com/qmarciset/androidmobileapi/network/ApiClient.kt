/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.network

import android.content.Context
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper
import com.qmarciset.androidmobileapi.auth.AuthenticationInterceptor
import com.qmarciset.androidmobileapi.auth.LoginRequiredCallback
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object ApiClient {

    const val AUTHORIZATION_HEADER_KEY = "Authorization"
    const val AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer"
    const val CONTENT_TYPE_HEADER_KEY = "Content-Type"
    const val CONTENT_TYPE_HEADER_VALUE = "application/json"
    const val X_QMOBILE_HEADER_KEY = "X-QMobile"
    const val X_QMOBILE_HEADER_VALUE = "1"
    private const val SERVER_ENDPOINT = "/mobileapp/"
    private const val REQUEST_TIMEOUT = 30

    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null

    private lateinit var authInfoHelper: AuthInfoHelper

    // For Singleton instantiation
    @Volatile
    var LOGIN_INSTANCE: LoginApiService? = null

    @Volatile
    var INSTANCE: ApiService? = null

    /**
     * Gets or generates the ApiClient
     */
    fun getApiService(
        context: Context,
        baseUrl: String = "",
        loginApiService: LoginApiService,
        loginRequiredCallback: LoginRequiredCallback
    ): ApiService {
        INSTANCE?.let {
            return it
        } ?: kotlin.run {
            val service =
                getClient(context, baseUrl, loginApiService, loginRequiredCallback).create(
                    ApiService::class.java
                )
            INSTANCE = service
            Timber.i("ApiService created")
            return service
        }
    }

    /**
     * Gets or generates the LoginApiClient
     */
    fun getLoginApiService(context: Context, baseUrl: String = ""): LoginApiService {
        LOGIN_INSTANCE?.let {
            return it
        } ?: kotlin.run {
            val service = getClient(context, baseUrl, null, null).create(
                LoginApiService::class.java
            )
            LOGIN_INSTANCE = service
            Timber.i("LoginApiService created")
            return service
        }
    }

    private fun getClient(
        context: Context,
        baseUrl: String,
        loginApiService: LoginApiService?,
        loginRequiredCallback: LoginRequiredCallback?
    ): Retrofit {
        authInfoHelper = AuthInfoHelper.getInstance(context)

        retrofit?.let {
            return it
        } ?: kotlin.run {
            val newRetrofit = Retrofit.Builder()
                .baseUrl(
                    if (baseUrl.isEmpty())
                        buildUrl(authInfoHelper.remoteUrl)
                    else
                        baseUrl
                )
                .client(
                    okHttpClient ?: initOkHttp(
                        loginApiService,
                        loginRequiredCallback
                    )
                )
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            retrofit = newRetrofit
            return newRetrofit
        }
    }

    /**
     * Clears instances to refresh their build with updated remoteUrl
     */
    fun clearApiClients() {
        retrofit = null
        okHttpClient = null
        INSTANCE = null
        LOGIN_INSTANCE = null
    }

    /**
     * Sets the interceptors for requests
     */
    private fun initOkHttp(
        loginApiService: LoginApiService?,
        loginRequiredCallback: LoginRequiredCallback?
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        okHttpClientBuilder.addInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )

        // Adds authentication interceptor
        okHttpClientBuilder.addInterceptor(
            AuthenticationInterceptor(authInfoHelper, loginApiService, loginRequiredCallback)
        )

        val newOkHttpClient = okHttpClientBuilder.build()
        okHttpClient = newOkHttpClient
        return newOkHttpClient
    }

    /**
     * Adjusts retrofit baseUrl depending on what is given in remoteUrl
     */
    private fun buildUrl(remoteUrl: String): String = remoteUrl.removeSuffix("/") + SERVER_ENDPOINT
}
