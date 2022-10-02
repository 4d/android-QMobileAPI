/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.network

import com.fasterxml.jackson.databind.ObjectMapper
import com.qmobile.qmobileapi.auth.AuthenticationInterceptor
import com.qmobile.qmobileapi.utils.LoginRequiredCallback
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val HTTP_PREFIX = "http://"
    private const val HTTPS_PREFIX = "https://"
    private const val SERVER_ENDPOINT = "/mobileapp/"
    const val REQUEST_TIMEOUT = 15

    private var retrofitLogin: Retrofit? = null
    var retrofit: Retrofit? = null
    private var retrofitAccessibility: Retrofit? = null
    private var okHttpClientLogin: OkHttpClient? = null
    private var okHttpClient: OkHttpClient? = null
    private var okHttpClientAccessibility: OkHttpClient? = null

    private lateinit var authenticationInterceptor: AuthenticationInterceptor

    // For Singleton instantiation
    @Volatile
    var LOGIN_INSTANCE: LoginApiService? = null

    @Volatile
    var INSTANCE: ApiService? = null

    @Volatile
    var ACCESSIBILITY_INSTANCE: AccessibilityApiService? = null

    /**
     * Gets or generates the ApiClient
     */
    fun getApiService(
        baseUrl: String = "",
        loginApiService: LoginApiService,
        loginRequiredCallback: LoginRequiredCallback,
        sharedPreferencesHolder: SharedPreferencesHolder,
        logBody: Boolean = false,
        mapper: ObjectMapper
    ): ApiService {
        INSTANCE?.let {
            return it
        } ?: kotlin.run {
            val service =
                getClient(
                    baseUrl,
                    loginApiService,
                    loginRequiredCallback,
                    sharedPreferencesHolder,
                    logBody,
                    mapper
                ).create(
                    ApiService::class.java
                )
            INSTANCE = service
            Timber.v("ApiService created")
            return service
        }
    }

    /**
     * Gets or generates the LoginApiClient
     */
    fun getLoginApiService(
        baseUrl: String = "",
        sharedPreferencesHolder: SharedPreferencesHolder,
        logBody: Boolean = false,
        mapper: ObjectMapper
    ): LoginApiService {
        LOGIN_INSTANCE?.let {
            return it
        } ?: kotlin.run {
            val service = getClientLogin(baseUrl, sharedPreferencesHolder, logBody, mapper).create(
                LoginApiService::class.java
            )
            LOGIN_INSTANCE = service
            Timber.v("LoginApiService created")
            return service
        }
    }

    fun getAccessibilityApiService(
        baseUrl: String = "",
        sharedPreferencesHolder: SharedPreferencesHolder,
        logBody: Boolean = false,
        mapper: ObjectMapper
    ): AccessibilityApiService {
        ACCESSIBILITY_INSTANCE?.let {
            return it
        } ?: kotlin.run {
            val service =
                getClientAccessibility(baseUrl, sharedPreferencesHolder, logBody, mapper).create(
                    AccessibilityApiService::class.java
                )
            ACCESSIBILITY_INSTANCE = service
            Timber.v("AccessibilityApiService created")
            return service
        }
    }

    /**
     * Builds a retrofit client for ApiService
     */
    private fun getClient(
        baseUrl: String,
        loginApiService: LoginApiService,
        loginRequiredCallback: LoginRequiredCallback,
        sharedPreferencesHolder: SharedPreferencesHolder,
        logBody: Boolean,
        mapper: ObjectMapper
    ): Retrofit {
        retrofit?.let {
            return it
        }
        val newRetrofit = Retrofit.Builder()
            .baseUrl(
                baseUrl.ifEmpty { buildUrl(sharedPreferencesHolder.remoteUrl) }
            )
            .client(
                okHttpClient ?: initOkHttp(
                    loginApiService,
                    loginRequiredCallback,
                    sharedPreferencesHolder,
                    logBody,
                    mapper
                )
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        retrofit = newRetrofit
        return newRetrofit
    }

    /**
     * Builds a retrofit client for LoginApiService
     */
    private fun getClientLogin(
        baseUrl: String,
        sharedPreferencesHolder: SharedPreferencesHolder,
        logBody: Boolean,
        mapper: ObjectMapper
    ): Retrofit {
        retrofitLogin?.let {
            return it
        }
        val newRetrofit = Retrofit.Builder()
            .baseUrl(
                baseUrl.ifEmpty { buildUrl(sharedPreferencesHolder.remoteUrl) }
            )
            .client(
                okHttpClientLogin ?: initOkHttp(
                    null,
                    null,
                    sharedPreferencesHolder,
                    logBody,
                    mapper
                )
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        retrofitLogin = newRetrofit
        return newRetrofit
    }

    private fun getClientAccessibility(
        baseUrl: String,
        sharedPreferencesHolder: SharedPreferencesHolder,
        logBody: Boolean,
        mapper: ObjectMapper
    ): Retrofit {
        retrofitAccessibility?.let {
            return it
        }
        val newRetrofit = Retrofit.Builder()
            .baseUrl(
                baseUrl.ifEmpty { buildUrl(sharedPreferencesHolder.remoteUrl) }
            )
            .client(
                okHttpClientAccessibility ?: initOkHttp(
                    null,
                    null,
                    sharedPreferencesHolder,
                    logBody,
                    mapper
                )
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        retrofitAccessibility = newRetrofit
        return newRetrofit
    }

    /**
     * Clears instances to refresh their build with updated remoteUrl
     */
    fun clearApiClients() {
        retrofit = null
        okHttpClient = null
        INSTANCE = null
        retrofitLogin = null
        okHttpClientLogin = null
        LOGIN_INSTANCE = null
        retrofitAccessibility = null
        okHttpClientAccessibility = null
        ACCESSIBILITY_INSTANCE = null
    }

    /**
     * Sets the interceptors for requests
     */
    private fun initOkHttp(
        loginApiService: LoginApiService?,
        loginRequiredCallback: LoginRequiredCallback?,
        sharedPreferencesHolder: SharedPreferencesHolder,
        logBody: Boolean,
        mapper: ObjectMapper
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val hostName =
            sharedPreferencesHolder.remoteUrl.removePrefix(HTTP_PREFIX).removePrefix(HTTPS_PREFIX).removeSuffix("/")

        val clientCertificates = HandshakeCertificates.Builder()
            .addPlatformTrustedCertificates()
            .addInsecureHost(hostName)
            .build()
        okHttpClientBuilder.sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager)

        okHttpClientBuilder.hostnameVerifier { host, _ ->
            host == hostName
        }

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

        // Adds authentication interceptor
        authenticationInterceptor =
            AuthenticationInterceptor(
                sharedPreferencesHolder,
                loginApiService,
                loginRequiredCallback,
                mapper
            )
        okHttpClientBuilder.addInterceptor(authenticationInterceptor)

        val newOkHttpClient = okHttpClientBuilder.build()
        if (loginApiService != null) {
            okHttpClient = newOkHttpClient
        } else {
            okHttpClientLogin = newOkHttpClient
        }
        return newOkHttpClient
    }

    /**
     * Adjusts retrofit baseUrl depending on what is given in remoteUrl
     */
    fun buildUrl(remoteUrl: String): String {
        var url = remoteUrl.removePrefix("/").removeSuffix("/")

        if (!(url.startsWith(HTTP_PREFIX) || url.startsWith(HTTPS_PREFIX))) {
            url = "$HTTPS_PREFIX$url"
        }

        return url.removeSuffix("/") + SERVER_ENDPOINT
    }

    /**
     * Lets MainActivity interact with AuthenticationInterceptor
     */
    fun dataSyncFinished() {
        authenticationInterceptor.reinitializeInterceptorRetryState()
    }
}
