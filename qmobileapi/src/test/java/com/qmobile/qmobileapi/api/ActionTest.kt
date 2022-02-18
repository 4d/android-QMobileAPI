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
import com.google.gson.JsonSyntaxException
import com.qmobile.qmobileapi.model.action.ActionResponse
import com.qmobile.qmobileapi.model.action.UploadImageResponse
import com.qmobile.qmobileapi.network.ApiClient
import com.qmobile.qmobileapi.network.ApiService
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.LoginRequiredCallback
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import com.qmobile.qmobileapi.utils.assertRequest
import com.qmobile.qmobileapi.utils.assertResponseSuccessful
import com.qmobile.qmobileapi.utils.mockResponse
import com.qmobile.qmobileapi.utils.parseToType
import okhttp3.RequestBody
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
        val loginRequiredCallback: LoginRequiredCallback = {}

        synchronized(ApplicationProvider.getApplicationContext()) {
            ApiClient.clearApiClients()
            apiService = ApiClient.getApiService(
                mockWebServer.url("/").toString(),
                loginApiService,
                loginRequiredCallback,
                SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext()),
                mapper = mapper
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
                    "/\$upload?\$rawPict=true" -> {
                        mockResponse("uploadImageSuccess.json")
                    }
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }
    }

    @Test
    fun `send action without params success`() {
        val response: Response<ResponseBody> =
            apiService.sendAction("action1", mutableMapOf()).blockingGet()
        val json = response.body()?.string()
        assertNotNull(json)
        assertResponseSuccessful(response)
        val actionResponse =
            try {
                mapper.parseToType<ActionResponse>(json.toString())
            } catch (e: JsonSyntaxException) {
                Timber.w("Failed to decode auth response ${e.message}: $json")
                null
            }
        assertNotNull(actionResponse)
        actionResponse?.let {
            assertTrue(it.success)
            assertNull(it.statusText)
        }
    }

    @Test
    fun `send action without params fail`() {
        val response: Response<ResponseBody> =
            apiService.sendAction("action2", mutableMapOf()).blockingGet()
        val json = response.body()?.string()
        assertNotNull(json)
        assertResponseSuccessful(response)
        val actionResponse =
            try {
                mapper.parseToType<ActionResponse>(json.toString())
            } catch (e: JsonSyntaxException) {
                Timber.w("Failed to decode auth response ${e.message}: $json")
                null
            }
        assertNotNull(actionResponse)
        actionResponse?.let {
            assertFalse(it.success)
            assertNotNull(it.statusText)
            assertNotEquals("", it.statusText)
        }
    }

    @Test
    fun `send action with params success`() {
        val response: Response<ResponseBody> =
            apiService.sendAction("action1", createActionContent()).blockingGet()
        val json = response.body()?.string()
        assertNotNull(json)
        assertResponseSuccessful(response)
        val actionResponse =
            try {
                mapper.parseToType<ActionResponse>(json.toString())
            } catch (e: JsonSyntaxException) {
                Timber.w("Failed to decode auth response ${e.message}: $json")
                null
            }
        assertNotNull(actionResponse)
        actionResponse?.let {
            assertTrue(it.success)
            assertNull(it.statusText)
        }
    }

    @Test
    fun `send action with params fail`() {
        val response: Response<ResponseBody> =
            apiService.sendAction("action2", createActionContent()).blockingGet()
        val json = response.body()?.string()
        assertNotNull(json)
        assertResponseSuccessful(response)
        val actionResponse =
            try {
                mapper.parseToType<ActionResponse>(json.toString())
            } catch (e: JsonSyntaxException) {
                Timber.w("Failed to decode auth response ${e.message}: $json")
                null
            }
        assertNotNull(actionResponse)
        actionResponse?.let {
            assertFalse(it.success)
            assertNotNull(it.statusText)
            assertNotEquals("", it.statusText)
        }
    }

    @Test
    fun `upload image params success`() {
        val requestBody: RequestBody = Mockito.mock(RequestBody::class.java)
        val response: Response<ResponseBody> =
            apiService.uploadImage("image/png", "\$upload?\$rawPict=true", requestBody)
                .blockingGet()
        val json = response.body()?.string()
        assertNotNull(json)
        assertResponseSuccessful(response)
        val actionResponse =
            try {
                mapper.parseToType<UploadImageResponse>(json.toString())
            } catch (e: JsonSyntaxException) {
                Timber.w("Failed to decode response ${e.localizedMessage}: $json")
                null
            }
        assertNotNull(actionResponse)
        actionResponse?.let {
            assertNotEquals("", it.id)
        }
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    private fun createActionContent(): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = mutableMapOf()
        val actionContext = mutableMapOf<String, Any>(
            "dataClass" to
                "Employee"
        )
        map["context"] = actionContext

        val parameters = HashMap<String, Any>()
        parameters["Text param"] = "azerty"
        parameters["Boolean param"] = false
        parameters["Number param"] = 4.0F
        parameters["Date param"] = "10!6!2000"
        map["parameters"] = parameters

        val metadata = HashMap<String, Any>()
        metadata["parameters"] = hashMapOf("Date param" to "simpleDate")
        map["metadata"] = metadata

        return map
    }
}
