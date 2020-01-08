package com.qmarciset.androidmobileapi.network

import android.content.Context
import com.qmarciset.androidmobileapi.utils.BASE_URL
import com.qmarciset.androidmobileapi.utils.COOKIE
import com.qmarciset.androidmobileapi.utils.CookieHelper
import com.qmarciset.androidmobileapi.utils.REQUEST_TIMEOUT
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object ApiClient {

    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null

    // For Singleton instantiation
    @Volatile
    var INSTANCE: ApiService? = null

    fun getApiService(baseUrl: String = BASE_URL, context: Context): ApiService {
        INSTANCE?.let {
            return it
        } ?: kotlin.run {
            return getClient(baseUrl, context).create(ApiService::class.java)
        }
    }

    private fun getClient(baseUrl: String = BASE_URL, context: Context): Retrofit {
        Timber.d("getClient: ")

        retrofit?.let {
            return it
        } ?: kotlin.run {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(
                    okHttpClient
                        ?: initOkHttp(
                            context
                        )
                )
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
    }

    private fun initOkHttp(context: Context): OkHttpClient {
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
                .addHeader("Content-Type", "application/json")

            CookieHelper.getCookieFromPref(context)
                ?.let {
                    Timber.d("Cookie found")
                    requestBuilder.addHeader(COOKIE, it)
                }

            val request = requestBuilder.build()

            println("request = $request")
            println("request.headers  = ${request.headers}")
            val response = chain.proceed(request)

            when (response.code) {
                HttpURLConnection.HTTP_OK -> {
                    val cookieString = CookieHelper.buildCookieString(response.headers)
                    cookieString?.let {
                        CookieHelper.saveLastOkRequestCookieInPref(context, cookieString)
                    }
                }
                HttpURLConnection.HTTP_PAYMENT_REQUIRED -> {
                    val lastCookie = CookieHelper.getLastOkRequestCookieFromPref(context)
                    CookieHelper.saveCookieInPref(context, lastCookie)
                }
            }
            response
        }
        return httpClient.build()
    }
}
