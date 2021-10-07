/*
 * Created by Quentin Marciset on 23/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.api

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.model.auth.LogoutResponse
import com.qmobile.qmobileapi.model.auth.UserInfo
import com.qmobile.qmobileapi.network.ApiClient
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.APP_JSON
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import com.qmobile.qmobileapi.utils.UTF8_CHARSET
import com.qmobile.qmobileapi.utils.assertRequest
import com.qmobile.qmobileapi.utils.assertResponseSuccessful
import com.qmobile.qmobileapi.utils.mockResponse
import com.qmobile.qmobileapi.utils.parseJsonToType
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
import java.net.HttpURLConnection

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class LoginApiTest {

    private var mockWebServer = MockWebServer()
    private lateinit var loginApiService: LoginApiService
    private lateinit var dispatcher: Dispatcher
    private var gson = Gson()

    @Before
    fun setup() {
        println("prepareTest")
        initDispatcher()
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start()

        synchronized(ApplicationProvider.getApplicationContext()) {
            ApiClient.clearApiClients()
            loginApiService =
                ApiClient.getLoginApiService(
                    mockWebServer.url("/").toString(),
                    SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext())
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
    fun `post authenticate`() {
        // Action /$authenticate
        val jsonRequest = JSONObject().apply {
            put(SharedPreferencesHolder.AUTH_EMAIL, "azeaze")
            put(SharedPreferencesHolder.AUTH_PASSWORD, "azezeaearaze")
        }
        val body = jsonRequest.toString()
            .toRequestBody("$APP_JSON; $UTF8_CHARSET".toMediaTypeOrNull())
        val response: Response<ResponseBody> = loginApiService.authenticate(body).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val authResponse = gson.parseJsonToType<AuthResponse>(json)
        assertEquals("REGLAZRERGHKLG", authResponse?.id)
        assertEquals("ZAGHEGRJRLA", authResponse?.token)
        val userInfo = gson.parseJsonToType<UserInfo>(authResponse?.userInfo.toString())
        assertEquals("azeaze", userInfo?.name)
        assertEquals("azezeaearaze", userInfo?.email)
    }

    @Test
    fun `post logout`() {
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
    fun `post synchronous authenticate`() {
        // Action /$authenticate
        val jsonRequest = JSONObject().apply {
            put(SharedPreferencesHolder.AUTH_EMAIL, "azeaze")
            put(SharedPreferencesHolder.AUTH_PASSWORD, "azezeaearaze")
        }
        val body = jsonRequest.toString()
            .toRequestBody("$APP_JSON; $UTF8_CHARSET".toMediaTypeOrNull())
        val response = loginApiService.syncAuthenticate(body).execute()
        assertResponseSuccessful(response)

        val authResponse = response.body()
        assertEquals("REGLAZRERGHKLG", authResponse?.id)
        assertEquals("ZAGHEGRJRLA", authResponse?.token)
        val userInfo = gson.parseJsonToType<UserInfo>(authResponse?.userInfo.toString())
        assertEquals("azeaze", userInfo?.name)
        assertEquals("azezeaearaze", userInfo?.email)
    }

    @Test
    fun `post synchronous logout`() {
        // Action /$logout
        val response = loginApiService.syncLogout().execute()
        assertResponseSuccessful(response)

        val logoutResponse = response.body()
        assertEquals(true, logoutResponse?.ok)
    }
}
