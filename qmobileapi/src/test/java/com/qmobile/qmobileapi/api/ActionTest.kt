/*
 * Created by htemanni on 10/9/2021.
 * 4D SAS
 * Copyright (c) 2021 htemanni. All rights reserved.
 */

package com.qmobile.qmobileapi.api

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.qmobile.qmobileapi.auth.LoginRequiredCallback
import com.qmobile.qmobileapi.model.action.ActionContent
import com.qmobile.qmobileapi.model.action.ActionResponse
import com.qmobile.qmobileapi.network.ApiClient
import com.qmobile.qmobileapi.network.ApiService
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.*
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ActionTest {

    private var mockWebServer = MockWebServer()
    private lateinit var apiService: ApiService
    private lateinit var dispatcher: Dispatcher
    private val mapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerKotlinModule()
    @Before
    fun setup() {
        initDispatcher()
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start()

        val loginApiService: LoginApiService = Mockito.mock(LoginApiService::class.java)
        val loginRequiredCallback: LoginRequiredCallback =
            Mockito.mock(LoginRequiredCallback::class.java)

        synchronized(ApplicationProvider.getApplicationContext()) {
            ApiClient.clearApiClients()
            apiService = ApiClient.getApiService(
                mockWebServer.url("/").toString(),
                loginApiService,
                loginRequiredCallback,
                SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext()),
                mapper =mapper
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
                    // `test send action success`()
                    "/\$action/action1" -> {
                        mockResponse("actionSuccess.json")
                    }
                    // `test send action fail`()
                    "/\$action/action2" -> {
                        mockResponse("actionFail.json")
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
    fun `send action success`() {
        val response: Response<ResponseBody> =
            apiService.sendAction("action1", ActionContent(mapOf(Pair("", "")))).blockingGet()
        val json = response.body()?.string()
        assertNotNull(json)
        assertResponseSuccessful(response)
        val actionResponse =
            try {
                mapper.parseToType<ActionResponse>(json.toString())
            } catch (e: JsonSyntaxException) {
                Timber.w("Failed to decode auth response ${e.localizedMessage}: ${json.toString()}")
                null
            }

        actionResponse?.let {
            assertTrue(it.success)
            assertNull(it.statusText)
        }
    }

    @Test
    fun `send action fail`() {
        val response: Response<ResponseBody> =
            apiService.sendAction("action2", ActionContent(mapOf(Pair("", "")))).blockingGet()
        val json = response.body()?.string()
        assertNotNull(json)
        assertResponseSuccessful(response)
        val actionResponse =
            try {
                mapper.parseToType<ActionResponse>(json.toString())
            } catch (e: JsonSyntaxException) {
                Timber.w("Failed to decode auth response ${e.localizedMessage}: ${json.toString()}")
                null
            }
        actionResponse?.let {
            assertFalse(it.success)
            assertNotNull(it.statusText)
            assertNotEquals("", it.statusText)
        }
    }
}
