/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.qmarciset.androidmobileapi.auth.LoginRequiredCallback
import com.qmarciset.androidmobileapi.model.catalog.Catalog
import com.qmarciset.androidmobileapi.model.catalog.Kind
import com.qmarciset.androidmobileapi.model.catalog.Scope
import com.qmarciset.androidmobileapi.model.entity.Entities
import com.qmarciset.androidmobileapi.model.info.Info
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.ApiService
import com.qmarciset.androidmobileapi.network.LoginApiService
import com.qmarciset.androidmobileapi.utils.assertRequest
import com.qmarciset.androidmobileapi.utils.assertResponseSuccessful
import com.qmarciset.androidmobileapi.utils.mockResponse
import com.qmarciset.androidmobileapi.utils.model.Event
import com.qmarciset.androidmobileapi.utils.parseJsonToType
import java.net.HttpURLConnection
import junit.framework.TestCase
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ApiTest {

    private var mockWebServer = MockWebServer()
    private lateinit var apiService: ApiService
    private lateinit var dispatcher: Dispatcher
    private var gson = Gson()

    @Before
    fun prepareTest() {
        println("prepareTest")
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
                    // `test get catalog`()
                    "/\$catalog" -> {
                        mockResponse("restcatalog.json")
                    }
                    // `test get all dataClasses`()
                    "/\$catalog/\$all" -> {
                        mockResponse("restcatalogall.json")
                    }
                    // `test get dataClass`()
                    "/\$catalog/Event" -> {
                        mockResponse("restdataclass.json")
                    }
                    // `test get info`()
                    "/\$info" -> {
                        mockResponse("restinfo.json")
                    }
                    // `test get entity`()
                    "/Event(12)" -> {
                        mockResponse("restrecord.json")
                    }
                    // test get entity with attributes`()
                    "/Event(12)/id,title" -> {
                        mockResponse("restrecordattributes.json")
                    }
                    // `test get entities`()
                    "/Event" -> {
                        mockResponse("restrecords.json")
                    }
                    // `test get entities with attributes`()
                    "/Event/id,title" -> {
                        mockResponse("restrecordsattributes.json")
                    }
                    // `test get entities with filter`()
                    "/Event/?\$filter=%22id%3E13%20AND%20id%3C16%22" -> {
                        mockResponse("restrecordsfiltered.json")
                    }
                    // `test get entities with filter with attributes`()
                    "/Event/id,title/?\$filter=%22id%3E13%20AND%20id%3C16%22" -> {
                        mockResponse("restrecordsfilteredattributes.json")
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
    fun `test get catalog`() {
        // Action /$catalog
        val response: Response<ResponseBody> = apiService.getCatalog().blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val catalog = gson.parseJsonToType<Catalog>(json)
        assertEquals(1, catalog?.dataClasses?.size)
        assertEquals("Event", catalog?.dataClasses?.get(0)?.name)
    }

    @Test
    fun `test get all dataClasses`() {
        // Action /$catalog/$all
        val response: Response<ResponseBody> = apiService.getAllDataClasses().blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        TestCase.assertNotNull(json)

        val catalog = gson.parseJsonToType<Catalog>(json)
        assertEquals(1, catalog?.dataClasses?.size)
        assertEquals("Event", catalog?.dataClasses?.get(0)?.name)
        assertEquals(Scope.PUBLIC, catalog?.dataClasses?.get(0)?.scope)
    }

    @Test
    fun `test get dataClass`() {
        // Action /$catalog/Event
        val response: Response<ResponseBody> = apiService.getDataClass("Event").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        TestCase.assertNotNull(json)

        val catalog = gson.parseJsonToType<Catalog>(json)
        assertEquals("Event", catalog?.dataClasses?.get(0)?.name)
        assertEquals("EventCollection", catalog?.dataClasses?.get(0)?.collectionName)
        assertEquals(Scope.PUBLIC, catalog?.dataClasses?.get(0)?.scope)
        assertEquals("id", catalog?.dataClasses?.get(0)?.attributes?.get(0)?.name)
        assertEquals(Kind.STORAGE, catalog?.dataClasses?.get(0)?.attributes?.get(0)?.kind)
    }

    @Test
    fun `test get info`() {
        // Action /$info
        val response: Response<ResponseBody> = apiService.getInfo().blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val info = gson.parseJsonToType<Info>(json)
        assertEquals(4, info?.entitySetCount)
        assertEquals("Company", info?.entitySet?.get(0)?.tableName)
    }

    @Test
    fun `test get entity`() {
        // Action /Event(0)
        val response: Response<ResponseBody> =
            apiService.getEntity("Event", "12").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val event = gson.parseJsonToType<Event>(json)
        assertEquals(12, event?.id)
        assertEquals("event 1", event?.title)
    }

    @Test
    fun `test get entity with attributes`() {
        // Action /Event(0)/id,title
        val response: Response<ResponseBody> =
            apiService.getEntityWithAttributes("Event", "12", "id,title").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val event = gson.parseJsonToType<Event>(json)
        assertEquals(12, event?.id)
        assertEquals("event 1", event?.title)
        assertNull(event?.timeStamp)
        assertNull(event?.count)
    }

    @Test
    fun `test get entities`() {
        // Action /Event
        val response: Response<ResponseBody> = apiService.getEntities("Event").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<Event>>(entities?.__ENTITIES)
        assertEquals("Twelfth Event", events?.get(0)?.title)
    }

    @Test
    fun `test get entities with attributes`() {
        // Action /Event/id,title
        val response: Response<ResponseBody> =
            apiService.getEntitiesWithAttributes("Event", "id,title").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(3, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<Event>>(entities?.__ENTITIES)
        assertEquals("Twelfth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)
    }

    @Test
    fun `test get entities with filter`() {
        // Action /Event/$filter="id>13 AND id<16"
        val response: Response<ResponseBody> =
            apiService.getEntitiesFiltered("Event", "\"id>13 AND id<16\"").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(2, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<Event>>(entities?.__ENTITIES)
        assertEquals("Fourteenth Event", events?.get(0)?.title)
    }

    @Test
    fun `test get entities with filter with attributes`() {
        // Action /Event/id,title/$filter="id>13 AND id<16"
        val response: Response<ResponseBody> = apiService.getEntitiesFilteredWithAttributes(
            "Event",
            "id,title",
            "\"id>13 AND id<16\""
        ).blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals(2, entities?.__COUNT)
        assertEquals("Event", entities?.__entityModel)

        val events = gson.parseJsonToType<List<Event>>(entities?.__ENTITIES)
        assertEquals("Fourteenth Event", events?.get(0)?.title)
        assertNull(events?.get(0)?.timeStamp)
        assertNull(events?.get(0)?.count)
    }
}
