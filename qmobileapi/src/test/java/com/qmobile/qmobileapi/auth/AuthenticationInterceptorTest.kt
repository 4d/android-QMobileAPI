/*
 * Created by Quentin Marciset on 12/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.auth

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.network.ApiClient
import com.qmobile.qmobileapi.network.ApiService
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import com.qmobile.qmobileapi.utils.UNIT_TEST_TOKEN
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AuthenticationInterceptorTest {

    private var mockWebServer = MockWebServer()

    @Mock
    lateinit var mockedCall: Call<AuthResponse>

    @Mock
    lateinit var loginApiService: LoginApiService

    @Mock
    lateinit var loginRequiredCallback: LoginRequiredCallback

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    private fun buildApiService(sharedPreferencesHolder: SharedPreferencesHolder): ApiService {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
            .connectTimeout(ApiClient.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(ApiClient.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(ApiClient.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        okHttpClientBuilder.addInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )

        val mapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerKotlinModule()
        val authenticationInterceptor =
            AuthenticationInterceptor(sharedPreferencesHolder, loginApiService, loginRequiredCallback, mapper)
        okHttpClientBuilder.addInterceptor(authenticationInterceptor)

        val newOkHttpClient = okHttpClientBuilder.build()

        val newRetrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/").toString())
            .client(newOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return newRetrofit.create(ApiService::class.java)
    }

    @Test
    fun `errorQuery placeholder is missing or null`() {

        val authInfoHelper = SharedPreferencesHolder(ApplicationProvider.getApplicationContext())
        authInfoHelper.guestLogin = true

        // response
        val authResponse = AuthResponse(
            id = "",
            statusText = "",
            success = true,
            token = UNIT_TEST_TOKEN,
            userInfo = mapOf()
        )

        Mockito.`when`(loginApiService.syncAuthenticate(Mockito.any(RequestBody::class.java)))
            .thenReturn(mockedCall)

        Mockito.doAnswer {
            Response.success(authResponse)
        }.`when`(mockedCall).execute()

        var firstTime = true

        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("Request = $request")

                return when (request.path) {

                    "/Event(12)" -> {

                        Assert.assertEquals(
                            ApiClient.CONTENT_TYPE_HEADER_VALUE,
                            request.headers[ApiClient.CONTENT_TYPE_HEADER_KEY]
                        )
                        Assert.assertEquals(
                            ApiClient.X_QMOBILE_HEADER_VALUE,
                            request.headers[ApiClient.X_QMOBILE_HEADER_KEY]
                        )

                        val responseCode =
                            MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
                        if (firstTime) {
                            Assert.assertEquals(
                                null,
                                request.headers[ApiClient.AUTHORIZATION_HEADER_KEY]
                            )
                            responseCode.setBody(
                                "{\"success\": false, \"__ERRORS\": [{\"message\":\"wrong request\", \"componentSignature\":\"\", \"errCode\":1279}]}"
                            )
                        } else {
                            Assert.assertEquals(
                                "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} $UNIT_TEST_TOKEN",
                                request.headers[ApiClient.AUTHORIZATION_HEADER_KEY]
                            )
                        }
                        firstTime = false
                        return responseCode
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }

        val apiService = buildApiService(authInfoHelper)

        mockWebServer.dispatcher = dispatcher
        val response: Response<ResponseBody> =
            apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        Assert.assertNotNull(response.body())
        Assert.assertEquals(HttpURLConnection.HTTP_OK, response.code())
        Assert.assertEquals(true, response.isSuccessful)
        Assert.assertEquals(null, response.errorBody())
    }

    @Test
    fun `header with session token`() {

        val authInfoHelper = SharedPreferencesHolder(ApplicationProvider.getApplicationContext())
        authInfoHelper.sessionToken = UNIT_TEST_TOKEN

        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("Request = $request")

                Assert.assertEquals(
                    ApiClient.CONTENT_TYPE_HEADER_VALUE,
                    request.headers[ApiClient.CONTENT_TYPE_HEADER_KEY]
                )
                Assert.assertEquals(
                    ApiClient.X_QMOBILE_HEADER_VALUE,
                    request.headers[ApiClient.X_QMOBILE_HEADER_KEY]
                )
                Assert.assertEquals(
                    "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} $UNIT_TEST_TOKEN",
                    request.headers[ApiClient.AUTHORIZATION_HEADER_KEY]
                )

                return when (request.path) {

                    "/Event(12)" -> {
                        return MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }

        val apiService = buildApiService(authInfoHelper)

        mockWebServer.dispatcher = dispatcher
        val response = apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        Assert.assertNotNull(response.body())
        Assert.assertEquals(HttpURLConnection.HTTP_OK, response.code())
        Assert.assertEquals(true, response.isSuccessful)
        Assert.assertEquals(null, response.errorBody())
    }

    @Test
    fun `http forbidden refreshAuth fails`() {

        val authInfoHelper = SharedPreferencesHolder(ApplicationProvider.getApplicationContext())
        authInfoHelper.guestLogin = true

        Mockito.`when`(loginApiService.syncAuthenticate(Mockito.any(RequestBody::class.java)))
            .thenReturn(mockedCall)

        Mockito.doAnswer {
            Response.success(null)
        }.`when`(mockedCall).execute()

        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("Request = $request")

                return when (request.path) {

                    "/Event(12)" -> {

                        Assert.assertEquals(
                            ApiClient.CONTENT_TYPE_HEADER_VALUE,
                            request.headers[ApiClient.CONTENT_TYPE_HEADER_KEY]
                        )
                        Assert.assertEquals(
                            ApiClient.X_QMOBILE_HEADER_VALUE,
                            request.headers[ApiClient.X_QMOBILE_HEADER_KEY]
                        )

                        val responseCode =
                            MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                        Assert.assertEquals(
                            null,
                            request.headers[ApiClient.AUTHORIZATION_HEADER_KEY]
                        )
                        return responseCode
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }

        val apiService = buildApiService(authInfoHelper)

        mockWebServer.dispatcher = dispatcher
        val response: Response<ResponseBody> =
            apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        Assert.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, response.code())
        Assert.assertEquals(false, response.isSuccessful)
    }

    @Test
    fun `http forbidden refreshAuth success`() {

        val authInfoHelper = SharedPreferencesHolder(ApplicationProvider.getApplicationContext())
        authInfoHelper.guestLogin = true

        // response
        val authResponse = AuthResponse(
            id = "",
            statusText = "",
            success = true,
            token = UNIT_TEST_TOKEN,
            userInfo = mapOf()
        )

        Mockito.`when`(loginApiService.syncAuthenticate(Mockito.any(RequestBody::class.java)))
            .thenReturn(mockedCall)

        Mockito.doAnswer {
            Response.success(authResponse)
        }.`when`(mockedCall).execute()

        var firstTime = true

        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("Request = $request")

                return when (request.path) {

                    "/Event(12)" -> {

                        Assert.assertEquals(
                            ApiClient.CONTENT_TYPE_HEADER_VALUE,
                            request.headers[ApiClient.CONTENT_TYPE_HEADER_KEY]
                        )
                        Assert.assertEquals(
                            ApiClient.X_QMOBILE_HEADER_VALUE,
                            request.headers[ApiClient.X_QMOBILE_HEADER_KEY]
                        )

                        val responseCode: MockResponse
                        if (firstTime) {
                            responseCode =
                                MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                            Assert.assertEquals(
                                null,
                                request.headers[ApiClient.AUTHORIZATION_HEADER_KEY]
                            )
                        } else {
                            responseCode = MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
                            Assert.assertEquals(
                                "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} $UNIT_TEST_TOKEN",
                                request.headers[ApiClient.AUTHORIZATION_HEADER_KEY]
                            )
                        }
                        firstTime = false
                        return responseCode
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }

        val apiService = buildApiService(authInfoHelper)

        mockWebServer.dispatcher = dispatcher
        val response: Response<ResponseBody> =
            apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        Assert.assertNotNull(response.body())
        Assert.assertEquals(HttpURLConnection.HTTP_OK, response.code())
        Assert.assertEquals(true, response.isSuccessful)
        Assert.assertEquals(null, response.errorBody())
    }
}
