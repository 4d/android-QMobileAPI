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
import com.qmobile.qmobileapi.model.entity.Entities
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
class ApiEntitiesTest {

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

                    // `test get entities`()
                    "/Event?\$limit=100000" -> {
                        mockResponse("restrecords.json")
                    }
                    // `test get entities with attributes`()
                    "/Event?\$attributes=id,title&\$limit=100000" -> {
                        mockResponse("restrecordsattributes.json")
                    }
                    // `test get entities with attributes and related entity attributes`()
                    "/Event?\$attributes=id,title,organizer.lastName,organizer.firstName&\$limit=100000" -> {
                        mockResponse("restrecordsrelatedentityattributes.json")
                    }
                    // `test get entities with attributes and related entities attributes`()
                    "/Event?\$attributes=id,title,guests.lastName,guests.firstName&\$limit=100000" -> {
                        mockResponse("restrecordsrelatedentitiesattributes.json")
                    }
                    // `test get entities with filter`()
                    "/Event?\$filter=%22id%3E13%20AND%20id%3C16%22&\$limit=100000" -> {
                        mockResponse("restrecordsfiltered.json")
                    }
                    // `test get entities with filter with attributes`()
                    "/Event?\$filter=%22id%3E13%20AND%20id%3C16%22&\$attributes=id,title&\$limit=100000" -> {
                        mockResponse("restrecordsfilteredattributes.json")
                    }
                    // `test get entities with filter with attributes and relations`()
                    "/Event?\$filter=%22id%3E13%20AND%20id%3C16%22&\$attributes=id,title,organizer.lastName,guests.lastName&\$limit=100000" -> {
                        mockResponse("restrecordsfilteredandrelations.json")
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
    fun `get entities`() {
        // Action /Event
        val response: Response<ResponseBody> =
            apiService.getEntities(dataClassName = "Event").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = mapper.parseToType<Entities<EventApiTest>>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = entities?.__ENTITIES
        assertEquals("Twelfth Event", events?.get(0)?.title)
    }

    @Test
    fun `get entities with attributes`() {
        // Action /Event?$attributes=id,title
        val response: Response<ResponseBody> =
            apiService.getEntities(dataClassName = "Event", attributes = "id,title").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = mapper.parseToType<Entities<EventApiTest>>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = entities?.__ENTITIES
        assertEquals("Twelfth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)
    }

    @Test
    fun `get entities with attributes and related entity attributes`() {
        // Action /Event?$attributes=id,title,organizer.lastName,organizer.firstName
        val response: Response<ResponseBody> =
            apiService.getEntities(
                dataClassName = "Event",
                attributes = "id,title,organizer.lastName,organizer.firstName"
            ).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = mapper.parseToType<Entities<EventApiTest>>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = entities?.__ENTITIES
        assertEquals("Twelfth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)

        val organizer = events?.get(0)?.organizer

        assertNotNull(organizer)
        organizer?.let {
            assertEquals("HANSEN", organizer.lastName)
            assertEquals("Yan", organizer.firstName)
        } ?: kotlin.run { Assert.fail() }
    }

    @Test
    fun `get entities with attributes and related entities attributes`() {
        // Action /Event?$attributes=id,title,guests.lastName,guests.firstName
        val response: Response<ResponseBody> =
            apiService.getEntities(
                dataClassName = "Event",
                attributes = "id,title,guests.lastName,guests.firstName"
            ).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = mapper.parseToType<Entities<EventApiTest>>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = entities?.__ENTITIES
        assertEquals("Twelfth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)

        val guestsEntities = events?.get(0)?.guests
        val guests = guestsEntities?.__ENTITIES
        assertNotNull(guests)
        assertEquals("BLOOMBERG", guests?.get(0)?.lastName)
        assertEquals("Roch", guests?.get(0)?.firstName)
        assertEquals("JONES", guests?.get(1)?.lastName)
        assertEquals("Jack", guests?.get(1)?.firstName)
        assertEquals(2, guests?.size)
    }

    @Test
    fun `get entities with filter`() {
        // Action /Event/$filter="id>13 AND id<16"
        val response: Response<ResponseBody> =
            apiService.getEntities(dataClassName = "Event", filter = "\"id>13 AND id<16\"")
                .blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = mapper.parseToType<Entities<EventApiTest>>(json)
        assertEquals(2, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = entities?.__ENTITIES
        assertEquals("Fourteenth Event", events?.get(0)?.title)
    }

    @Test
    fun `get entities with filter with attributes`() {
        // Action /Event?$filter="id>13 AND id<16"&$attributes=id,title
        val response: Response<ResponseBody> = apiService.getEntities(
            dataClassName = "Event",
            attributes = "id,title",
            filter = "\"id>13 AND id<16\""
        ).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = mapper.parseToType<Entities<EventApiTest>>(json)
        assertEquals(2, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = entities?.__ENTITIES
        assertEquals("Fourteenth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)
    }

    @Test
    fun `get entities with filter with attributes and relations`() {
        // Action /Event?$filter="id>13 AND id<16"&$attributes=id,title,organizer.lastName,guests.lastName
        val response: Response<ResponseBody> = apiService.getEntities(
            dataClassName = "Event",
            attributes = "id,title,organizer.lastName,guests.lastName",
            filter = "\"id>13 AND id<16\""
        ).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = mapper.parseToType<Entities<EventApiTest>>(json)
        assertEquals(2, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = entities?.__ENTITIES
        assertEquals("Fourteenth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)

        val organizer = events?.get(0)?.organizer

        assertNotNull(organizer)
        organizer?.let {
            assertEquals("COOK", organizer.lastName)
            assertNull(organizer.firstName)
        } ?: kotlin.run { Assert.fail() }

        val guestsEntities = events?.get(0)?.guests
        val guests = guestsEntities?.__ENTITIES
        assertNotNull(guests)
        assertEquals("BLOOMBERG", guests?.get(0)?.lastName)
        assertNull(guests?.get(0)?.firstName)
        assertEquals("JONES", guests?.get(1)?.lastName)
        assertNull(guests?.get(1)?.firstName)
        assertEquals(2, guests?.size)
    }
}
