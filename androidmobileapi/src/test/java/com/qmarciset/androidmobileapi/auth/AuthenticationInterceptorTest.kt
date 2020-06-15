/*
 * Created by Quentin Marciset on 12/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.auth

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.gson.JsonObject
import com.qmarciset.androidmobileapi.model.auth.AuthResponse
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.ApiService
import com.qmarciset.androidmobileapi.network.LoginApiService
import com.qmarciset.androidmobileapi.utils.UNIT_TEST_TOKEN
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
import java.net.HttpURLConnection

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AuthenticationInterceptorTest {

    private var mockWebServer = MockWebServer()
    private lateinit var apiService: ApiService

    @Mock
    lateinit var mockedCall: Call<AuthResponse>

    @Mock
    lateinit var loginApiService: LoginApiService

    @Mock
    lateinit var loginRequiredCallback: LoginRequiredCallback

    @Before
    fun prepareTest() {
        MockitoAnnotations.initMocks(this)

        ApiClient.clearApiClients()
        apiService = ApiClient.getApiService(
            ApplicationProvider.getApplicationContext(),
            mockWebServer.url("/").toString(),
            loginApiService,
            loginRequiredCallback
        )
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testErrorQuery_placeholder_is_missing_or_null() {

        val authInfoHelper = AuthInfoHelper.getInstance(ApplicationProvider.getApplicationContext())
        authInfoHelper.guestLogin = true

        // response
        val authResponse = AuthResponse(
            id = "",
            statusText = "",
            success = true,
            token = UNIT_TEST_TOKEN,
            userInfo = JsonObject()
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
//                            Assert.assertEquals(null, request.headers[ApiClient.AUTHORIZATION_HEADER_KEY])
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

        mockWebServer.dispatcher = dispatcher
        val response: Response<ResponseBody> =
            apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        Assert.assertNotNull(response.body())
        Assert.assertEquals(HttpURLConnection.HTTP_OK, response.code())
        Assert.assertEquals(true, response.isSuccessful)
        Assert.assertEquals(null, response.errorBody())
    }

    @Test
    fun testHeaderWithSessionToken() {

        val authInfoHelper = AuthInfoHelper.getInstance(ApplicationProvider.getApplicationContext())
        authInfoHelper.sessionToken = UNIT_TEST_TOKEN

        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("Request = $request")

                Assert.assertEquals(ApiClient.CONTENT_TYPE_HEADER_VALUE, request.headers[ApiClient.CONTENT_TYPE_HEADER_KEY])
                Assert.assertEquals(ApiClient.X_QMOBILE_HEADER_VALUE, request.headers[ ApiClient.X_QMOBILE_HEADER_KEY])
                Assert.assertEquals("${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} $UNIT_TEST_TOKEN", request.headers[ApiClient.AUTHORIZATION_HEADER_KEY])

                return when (request.path) {

                    "/Event(12)" -> {
                        return MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }

        mockWebServer.dispatcher = dispatcher
        val response = apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        Assert.assertNotNull(response.body())
        Assert.assertEquals(HttpURLConnection.HTTP_OK, response.code())
        Assert.assertEquals(true, response.isSuccessful)
        Assert.assertEquals(null, response.errorBody())
    }

    @Test
    fun testHttpForbiddenRefreshAuthFails() {

        val authInfoHelper = AuthInfoHelper.getInstance(ApplicationProvider.getApplicationContext())
        authInfoHelper.guestLogin = true

        Mockito.`when`(loginApiService.syncAuthenticate(Mockito.any(RequestBody::class.java))).thenReturn(mockedCall)

        Mockito.doAnswer {
            Response.success(null)
        }.`when`(mockedCall).execute()

        val dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("Request = $request")

                return when (request.path) {

                    "/Event(12)" -> {

                        Assert.assertEquals(ApiClient.CONTENT_TYPE_HEADER_VALUE, request.headers[ApiClient.CONTENT_TYPE_HEADER_KEY])
                        Assert.assertEquals(ApiClient.X_QMOBILE_HEADER_VALUE, request.headers[ ApiClient.X_QMOBILE_HEADER_KEY])

                        val responseCode = MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
//                        Assert.assertEquals(null, request.headers[ApiClient.AUTHORIZATION_HEADER_KEY])
                        return responseCode
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }

        mockWebServer.dispatcher = dispatcher
        val response: Response<ResponseBody> = apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        Assert.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, response.code())
        Assert.assertEquals(false, response.isSuccessful)
    }

    @Test
    fun testHttpForbiddenRefreshAuthSuccess() {

        val authInfoHelper = AuthInfoHelper.getInstance(ApplicationProvider.getApplicationContext())
        authInfoHelper.guestLogin = true

        // response
        val authResponse = AuthResponse(
            id = "",
            statusText = "",
            success = true,
            token = UNIT_TEST_TOKEN,
            userInfo = JsonObject()
        )

        Mockito.`when`(loginApiService.syncAuthenticate(Mockito.any(RequestBody::class.java))).thenReturn(mockedCall)

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

                        Assert.assertEquals(ApiClient.CONTENT_TYPE_HEADER_VALUE, request.headers[ApiClient.CONTENT_TYPE_HEADER_KEY])
                        Assert.assertEquals(ApiClient.X_QMOBILE_HEADER_VALUE, request.headers[ ApiClient.X_QMOBILE_HEADER_KEY])

                        val responseCode: MockResponse
                        if (firstTime) {
                            responseCode = MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                            Assert.assertEquals(null, request.headers[ApiClient.AUTHORIZATION_HEADER_KEY])
                        } else {
                            responseCode = MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
                            Assert.assertEquals("${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} $UNIT_TEST_TOKEN", request.headers[ApiClient.AUTHORIZATION_HEADER_KEY])
                        }
                        firstTime = false
                        return responseCode
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }

        mockWebServer.dispatcher = dispatcher
        val response: Response<ResponseBody> = apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        Assert.assertNotNull(response.body())
        Assert.assertEquals(HttpURLConnection.HTTP_OK, response.code())
        Assert.assertEquals(true, response.isSuccessful)
        Assert.assertEquals(null, response.errorBody())
    }
}
