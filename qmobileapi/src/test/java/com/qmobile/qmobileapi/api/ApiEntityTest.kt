/*
 * Created by Quentin Marciset on 15/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.api

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.qmobile.qmobileapi.network.ApiClient
import com.qmobile.qmobileapi.network.ApiService
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.EventApiTest
import com.qmobile.qmobileapi.utils.LoginRequiredCallback
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import com.qmobile.qmobileapi.utils.assertRequest
import com.qmobile.qmobileapi.utils.assertResponseSuccessful
import com.qmobile.qmobileapi.utils.mockResponse
import com.qmobile.qmobileapi.utils.parseToType
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert
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
class ApiEntityTest {

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
                    // `test get entity`()
                    "/Event(12)" -> {
                        mockResponse("restrecord.json")
                    }
                    // test get entity with attributes`()
                    "/Event(12)?\$attributes=id,title" -> {
                        mockResponse("restrecordattributes.json")
                    }
                    // `test get entity with attributes and related entity attributes`()
                    "/Event(12)?\$attributes=id,title,organizer.lastName,organizer.firstName" -> {
                        mockResponse("restrecordrelatedentityattributes.json")
                    }
                    // `test get entity with attributes and related entities attributes`()
                    "/Event(12)?\$attributes=id,title,guests.lastName,guests.firstName" -> {
                        mockResponse("restrecordrelatedentitiesattributes.json")
                    }
                    // `test get entity with all related attributes`()
                    "/Event(12)?\$attributes=guests.*" -> {
                        mockResponse("restrecordallrelatedattributes.json")
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
    fun `get entity`() {
        // Action /Event(12)
        val response: Response<ResponseBody> =
            apiService.getEntity(dataClassName = "Event", key = "12").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val event = mapper.parseToType<EventApiTest>(json)
        assertEquals(12, event?.id)
        assertEquals("event 1", event?.title)
    }

    @Test
    fun `get entity with attributes`() {
        // Action /Event(12)?$attribute=id,title
        val response: Response<ResponseBody> =
            apiService.getEntity(dataClassName = "Event", key = "12", attributes = "id,title")
                .blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val event = mapper.parseToType<EventApiTest>(json)
        assertEquals(12, event?.id)
        assertEquals("event 1", event?.title)
        assertNull(event?.timeStamp)
        assertNull(event?.count)
    }

    @Test
    fun `get entity with attributes and related entity attributes`() {
        // Action /Event(12)?$attributes=id,title,organizer.lastName,organizer.firstName
        val response: Response<ResponseBody> =
            apiService.getEntity(
                dataClassName = "Event",
                key = "12",
                attributes = "id,title,organizer.lastName,organizer.firstName"
            ).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val event = mapper.parseToType<EventApiTest>(json)
        assertEquals(12, event?.id)
        assertEquals("event 1", event?.title)
        assertNull(event?.timeStamp)
        assertNull(event?.count)
        assertNull(event?.guests)

        val organizer = event?.organizer

        assertNotNull(organizer)
        organizer?.let {
            assertEquals("HANSEN", organizer.lastName)
            assertEquals("Yan", organizer.firstName)
        } ?: kotlin.run { Assert.fail() }
    }

    @Test
    fun `get entity with attributes with related entities attributes`() {
        // Action /Event(12)/?$attributes=id,title,guests.lastName,guests.firstName
        val response: Response<ResponseBody> =
            apiService.getEntity(
                dataClassName = "Event",
                key = "12",
                attributes = "id,title,guests.lastName,guests.firstName"
            ).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val event = mapper.parseToType<EventApiTest>(json)
        assertEquals(12, event?.id)
        assertEquals("event 1", event?.title)

        val guestsEntities = event?.guests
        val guests = guestsEntities?.__ENTITIES
        assertNotNull(guests)
        assertEquals("BLOOMBERG", guests?.get(0)?.lastName)
        assertEquals("Roch", guests?.get(0)?.firstName)
        assertEquals("JONES", guests?.get(1)?.lastName)
        assertEquals("Jack", guests?.get(1)?.firstName)
        assertEquals(2, guests?.size)
    }

    @Test
    fun `get entity with all related attributes`() {
        // "/Event(12)?\$attributes=guests.*
        val response: Response<ResponseBody> =
            apiService.getEntity(dataClassName = "Event", key = "12", attributes = "guests.*")
                .blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val event = mapper.parseToType<EventApiTest>(json)
        assertNull(event?.id)
        assertNull(event?.title)

        val guestsEntities = event?.guests
        val guests = guestsEntities?.__ENTITIES

        assertNotNull(guests)
        assertEquals("BLOOMBERG", guests?.get(0)?.lastName)
        assertEquals("Roch", guests?.get(0)?.firstName)
        assertEquals("JONES", guests?.get(1)?.lastName)
        assertEquals("Jack", guests?.get(1)?.firstName)
        assertEquals(2, guests?.size)
    }
}
