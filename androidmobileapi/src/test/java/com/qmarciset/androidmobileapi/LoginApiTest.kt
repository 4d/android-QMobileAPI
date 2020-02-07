/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper
import com.qmarciset.androidmobileapi.model.auth.AuthResponse
import com.qmarciset.androidmobileapi.model.auth.LogoutResponse
import com.qmarciset.androidmobileapi.model.auth.UserInfo
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.LoginApiService
import com.qmarciset.androidmobileapi.utils.assertRequest
import com.qmarciset.androidmobileapi.utils.assertResponseSuccessful
import com.qmarciset.androidmobileapi.utils.mockResponse
import com.qmarciset.androidmobileapi.utils.parseJsonToType
import java.net.HttpURLConnection
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LoginApiTest {

    private var mockWebServer = MockWebServer()
    private lateinit var loginApiService: LoginApiService
    private lateinit var dispatcher: Dispatcher
    private var gson = Gson()

    @Before
    fun prepareTest() {
        println("prepareTest")
        initDispatcher()
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start()

        synchronized(ApplicationProvider.getApplicationContext()) {
            ApiClient.clearApiClients()
            loginApiService =
                ApiClient.getLoginApiService(
                    ApplicationProvider.getApplicationContext(),
                    mockWebServer.url("/").toString()
                )
        }
    }

    private fun initDispatcher() {
        dispatcher = object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                println("Request = $request")
                // Assert requests
                assertRequest(request)

                return when (request.path) {
                    // `test authenticate`()
                    "/\$authenticate" -> {
                        mockResponse("restauthenticate.json")
                    }
                    // `test logout`()
                    "/\$logout" -> {
                        mockResponse("restlogout.json")
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test authenticate`() {
        // Action /$authenticate
        val jsonRequest = JSONObject().apply {
            put(AuthInfoHelper.AUTH_EMAIL, "azeaze")
            put(AuthInfoHelper.AUTH_PASSWORD, "azezeaearaze")
        }
        val body = jsonRequest.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val response: Response<ResponseBody> = loginApiService.authenticate(body).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val authResponse = gson.parseJsonToType<AuthResponse>(json)
        assertEquals("REGLAZRERGHKLG", authResponse?.id)
        assertEquals("ZAGHEGRJRLA", authResponse?.token)

        val userInfo = gson.parseJsonToType<UserInfo>(authResponse?.userInfo)
        assertEquals("azeaze", userInfo?.name)
        assertEquals("azezeaearaze", userInfo?.email)
    }

    @Test
    fun `test logout`() {
        // Action /$logout
        val response: Response<ResponseBody> = loginApiService.logout().blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val logoutResponse = gson.parseJsonToType<LogoutResponse>(json)
        assertEquals(true, logoutResponse?.ok)
    }

    @Test
    fun `test synchronous authenticate`() {
        // Action /$authenticate
        val jsonRequest = JSONObject().apply {
            put(AuthInfoHelper.AUTH_EMAIL, "azeaze")
            put(AuthInfoHelper.AUTH_PASSWORD, "azezeaearaze")
        }
        val body = jsonRequest.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val response = loginApiService.syncAuthenticate(body).execute()
        assertResponseSuccessful(response)

        val authResponse = response.body()
        assertEquals("REGLAZRERGHKLG", authResponse?.id)
        assertEquals("ZAGHEGRJRLA", authResponse?.token)

        val userInfo = gson.parseJsonToType<UserInfo>(authResponse?.userInfo)
        assertEquals("azeaze", userInfo?.name)
        assertEquals("azezeaearaze", userInfo?.email)
    }

    @Test
    fun `test synchronous logout`() {
        // Action /$logout
        val response = loginApiService.syncLogout().execute()
        assertResponseSuccessful(response)

        val logoutResponse = response.body()
        assertEquals(true, logoutResponse?.ok)
    }
}
