/*
 * Created by Quentin Marciset on 15/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.api

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.qmarciset.androidmobileapi.auth.LoginRequiredCallback
import com.qmarciset.androidmobileapi.model.entity.Entities
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.ApiService
import com.qmarciset.androidmobileapi.network.LoginApiService
import com.qmarciset.androidmobileapi.utils.EmployeeApiTest
import com.qmarciset.androidmobileapi.utils.EventApiTest
import com.qmarciset.androidmobileapi.utils.assertRequest
import com.qmarciset.androidmobileapi.utils.assertResponseSuccessful
import com.qmarciset.androidmobileapi.utils.mockResponse
import com.qmarciset.androidmobileapi.utils.parseJsonToType
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
    private var gson = Gson()

    @Before
    fun prepareTest() {
        initDispatcher()
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start()

        val loginApiService: LoginApiService = Mockito.mock(LoginApiService::class.java)
        val loginRequiredCallback: LoginRequiredCallback =
            Mockito.mock(LoginRequiredCallback::class.java)

        synchronized(ApplicationProvider.getApplicationContext()) {
            ApiClient.clearApiClients()
            apiService = ApiClient.getApiService(
                ApplicationProvider.getApplicationContext(),
                mockWebServer.url("/").toString(),
                loginApiService,
                loginRequiredCallback
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
                    "/Event" -> {
                        mockResponse("restrecords.json")
                    }
                    // `test get entities with attributes`()
                    "/Event?\$attributes=id,title" -> {
                        mockResponse("restrecordsattributes.json")
                    }
                    // `test get entities with attributes and related entity attributes`()
                    "/Event?\$attributes=id,title,organizer.lastName,organizer.firstName" -> {
                        mockResponse("restrecordsrelatedentityattributes.json")
                    }
                    // `test get entities with attributes and related entities attributes`()
                    "/Event?\$attributes=id,title,guests.lastName,guests.firstName" -> {
                        mockResponse("restrecordsrelatedentitiesattributes.json")
                    }
                    // `test get entities with filter`()
                    "/Event?\$filter=%22id%3E13%20AND%20id%3C16%22" -> {
                        mockResponse("restrecordsfiltered.json")
                    }
                    // `test get entities with filter with attributes`()
                    "/Event?\$filter=%22id%3E13%20AND%20id%3C16%22&\$attributes=id,title" -> {
                        mockResponse("restrecordsfilteredattributes.json")
                    }
                    // `test get entities with filter with attributes and relations`()
                    "/Event?\$filter=%22id%3E13%20AND%20id%3C16%22&\$attributes=id,title,organizer.lastName,guests.lastName" -> {
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
    fun `test get entities`() {
        // Action /Event
        val response: Response<ResponseBody> =
            apiService.getEntities(dataClassName = "Event").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<EventApiTest>>(entities?.__ENTITIES)
        assertEquals("Twelfth Event", events?.get(0)?.title)
    }

    @Test
    fun `test get entities with attributes`() {
        // Action /Event?$attributes=id,title
        val response: Response<ResponseBody> =
            apiService.getEntities(dataClassName = "Event", attributes = "id,title").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<EventApiTest>>(entities?.__ENTITIES)
        assertEquals("Twelfth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)
    }

    @Test
    fun `test get entities with attributes and related entity attributes`() {
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

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<EventApiTest>>(entities?.__ENTITIES)
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
    fun `test get entities with attributes and related entities attributes`() {
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

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<EventApiTest>>(entities?.__ENTITIES)
        assertEquals("Twelfth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)

        val guestsEntities = events?.get(0)?.guests
        val guests = gson.parseJsonToType<List<EmployeeApiTest>>(guestsEntities?.__ENTITIES)
        assertNotNull(guests)
        assertEquals("BLOOMBERG", guests?.get(0)?.lastName)
        assertEquals("Roch", guests?.get(0)?.firstName)
        assertEquals("JONES", guests?.get(1)?.lastName)
        assertEquals("Jack", guests?.get(1)?.firstName)
        assertEquals(2, guests?.size)
    }

    @Test
    fun `test get entities with filter`() {
        // Action /Event/$filter="id>13 AND id<16"
        val response: Response<ResponseBody> =
            apiService.getEntities(dataClassName = "Event", filter = "\"id>13 AND id<16\"")
                .blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(2, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<EventApiTest>>(entities?.__ENTITIES)
        assertEquals("Fourteenth Event", events?.get(0)?.title)
    }

    @Test
    fun `test get entities with filter with attributes`() {
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

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(2, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<EventApiTest>>(entities?.__ENTITIES)
        assertEquals("Fourteenth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)
    }

    @Test
    fun `test get entities with filter with attributes and relations`() {
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

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(2, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<EventApiTest>>(entities?.__ENTITIES)
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
        val guests = gson.parseJsonToType<List<EmployeeApiTest>>(guestsEntities?.__ENTITIES)
        assertNotNull(guests)
        assertEquals("BLOOMBERG", guests?.get(0)?.lastName)
        assertNull(guests?.get(0)?.firstName)
        assertEquals("JONES", guests?.get(1)?.lastName)
        assertNull(guests?.get(1)?.firstName)
        assertEquals(2, guests?.size)
    }
}
