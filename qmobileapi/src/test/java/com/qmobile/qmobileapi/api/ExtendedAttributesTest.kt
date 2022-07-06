/*
 * Created by Quentin Marciset on 23/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.api

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.qmobile.qmobileapi.model.entity.Entities
import com.qmobile.qmobileapi.network.ApiClient
import com.qmobile.qmobileapi.network.ApiService
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.APP_JSON
import com.qmobile.qmobileapi.utils.LoginRequiredCallback
import com.qmobile.qmobileapi.utils.ServiceExtendedAttributes
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import com.qmobile.qmobileapi.utils.UTF8_CHARSET
import com.qmobile.qmobileapi.utils.assertRequest
import com.qmobile.qmobileapi.utils.assertResponseSuccessful
import com.qmobile.qmobileapi.utils.mockResponse
import com.qmobile.qmobileapi.utils.parseToType
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response
import java.net.HttpURLConnection

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ExtendedAttributesTest {

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
                baseUrl = mockWebServer.url("/").toString(),
                loginApiService = loginApiService,
                loginRequiredCallback = loginRequiredCallback,
                sharedPreferencesHolder = SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext()),
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
                    // `test extendedAttributes true or false is the same`()
                    "/Service?\$filter=%22name%3DSponsorship%22&\$extendedAttributes=true&\$limit=100000" -> {
                        mockResponse("extendedattributes.json")
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
    fun `post extendedAttributes`() {
        // Action /Service?$filter="name=Sponsorship"&$extendedAttributes=true
        val bodyString =
            JSONObject("{\"name\":true,\"employees\":{\"__Query\":{\"queryString\":\"FirstName=Joel\"},\"FirstName\":true},\"manager\":{\"__Query\":\"FirstName=Joel\",\"*\":true}}")
        val body = bodyString.toString()
            .toRequestBody("$APP_JSON; $UTF8_CHARSET".toMediaTypeOrNull())

        val response: Response<ResponseBody> = apiService.getEntitiesExtendedAttributes(
            body = body,
            dataClassName = "Service",
            filter = "\"name=Sponsorship\""
        ).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = mapper.parseToType<Entities<ServiceExtendedAttributes>>(json)
        assertEquals("Service", entities?.__entityModel)
        val service = entities?.__ENTITIES?.get(0)
        assertNull(service?.employeeNumber)
        assertNotNull(service?.manager)
        assertNotNull(service?.employees)

        val manager = service?.manager
        assertEquals("Joel", manager?.FirstName)
        assertEquals("Price", manager?.LastName)
        assertEquals("Klean Corp", manager?.Company)
        assertNotNull(manager?.Location)
        assertNotNull(manager?.Notes)

        val employees = service?.employees?.__ENTITIES
        assertEquals(1, employees?.size)
        val employee = employees?.get(0)

        assertEquals("Joel", employee?.FirstName)
        assertNull(employee?.LastName)
        assertNull(employee?.Job)
        assertNull(employee?.Company)
        assertNull(employee?.Address)
    }
}
