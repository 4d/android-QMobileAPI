package com.qmarciset.androidmobileapi.network

import android.content.Context
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper.Companion.COOKIE
import com.qmarciset.androidmobileapi.utils.COOKIES_ARE_HANDLED
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object ApiClient {

    private const val AUTHORIZATION_HEADER_KEY = "Authorization"
    private const val AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer"
    private const val CONTENT_TYPE_HEADER_KEY = "Content-Type"
    private const val CONTENT_TYPE_HEADER_VALUE = "application/json"
    private const val X_QMOBILE_HEADER_KEY = "X-QMobile"
    private const val X_QMOBILE_HEADER_VALUE = "1"
    private const val SERVER_ENDPOINT = "/mobileapp/"
    private const val REQUEST_TIMEOUT = 30

    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null

    //     For Singleton instantiation
    @Volatile
    var INSTANCE: ApiService? = null

    fun getApiService(context: Context, baseUrl: String = ""): ApiService {
        INSTANCE?.let {
            return it
        } ?: kotlin.run {
            return getClient(context, baseUrl).create(ApiService::class.java)
        }
    }

    private fun getClient(context: Context, baseUrl: String): Retrofit {
        Timber.d("getClient: ")
        val authInfoHelper = AuthInfoHelper.getInstance(context)

        retrofit?.let {
            return it
        } ?: kotlin.run {
            return Retrofit.Builder()
                .baseUrl(
                    if (baseUrl.isEmpty())
                        buildUrl(authInfoHelper.remoteUrl)
                    else
                        baseUrl
                )
                .client(
                    okHttpClient
                        ?: initOkHttp(
                            authInfoHelper.sessionToken
                        )
                )
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
    }

    private fun initOkHttp(sessionToken: String, cookie: String = ""): OkHttpClient {
        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .addHeader(CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VALUE)
                .addHeader(X_QMOBILE_HEADER_KEY, X_QMOBILE_HEADER_VALUE)

            if (sessionToken.isNotEmpty()) {
                Timber.d("Setting retrieved token in header : $sessionToken")
                requestBuilder.addHeader(
                    AUTHORIZATION_HEADER_KEY,
                    "$AUTHORIZATION_HEADER_VALUE_PREFIX $sessionToken"
                )
            } else {
                Timber.d("No token was retrieved")
            }

            if (COOKIES_ARE_HANDLED) {
                requestBuilder.addHeader(COOKIE, cookie)
            }

            val request = requestBuilder.build()

            println("request = $request")
            println("request.headers  = ${request.headers}")
            val response = chain.proceed(request)

            when (response.code) {
                HttpURLConnection.HTTP_OK -> { }
                HttpURLConnection.HTTP_PAYMENT_REQUIRED -> { }
            }
            response
        }
        return httpClient.build()
    }

    private fun buildUrl(remoteUrl: String): String = remoteUrl.removeSuffix("/") + SERVER_ENDPOINT
}
