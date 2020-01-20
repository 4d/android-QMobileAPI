package com.qmarciset.androidmobileapi

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.qmarciset.androidmobileapi.model.catalog.Catalog
import com.qmarciset.androidmobileapi.model.catalog.Kind
import com.qmarciset.androidmobileapi.model.catalog.Scope
import com.qmarciset.androidmobileapi.model.entity.Entities
import com.qmarciset.androidmobileapi.model.info.Info
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.ApiService
import com.qmarciset.androidmobileapi.utils.getTestHeaders
import com.qmarciset.androidmobileapi.utils.model.Event
import com.qmarciset.androidmobileapi.utils.parseJsonToType
import com.qmarciset.androidmobileapi.utils.readContentFromFilePath
import java.net.HttpURLConnection
import junit.framework.TestCase
import okhttp3.ResponseBody
import okhttp3.internal.toHeaderList
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
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class APIServiceTestMockedServer {

    private var mockWebServer = MockWebServer()
    private lateinit var apiServiceMocked: ApiService
    private lateinit var dispatcher: Dispatcher
    private var gson = Gson()

    @Before
    fun prepareTest() {
        println("prepareTest")
        initDispatcher()
        mockWebServer.dispatcher = dispatcher
        mockWebServer.start()

        apiServiceMocked = ApiClient.getApiService(
            ApplicationProvider.getApplicationContext(),
            mockWebServer.url("/").toString()
        )
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

    private fun assertRequest(request: RecordedRequest) {
        assertEquals("${request.method} ${request.path} HTTP/1.1", request.requestLine)
        assertEquals("application/json", request.getHeader("Content-Type"))
        assertEquals("", request.body.readUtf8())
    }

    private fun mockResponse(filename: String): MockResponse {
        val responseCode = MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
        return responseCode.apply {
            responseCode.headers =
                getTestHeaders()
        }
            .setBody(
                readContentFromFilePath(
                    ApplicationProvider.getApplicationContext(),
                    filename
                )
            )
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test get catalog`() {
        // Action /$catalog
        val response: Response<ResponseBody> = apiServiceMocked.getCatalog().blockingGet()
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
        val response: Response<ResponseBody> = apiServiceMocked.getAllDataClasses().blockingGet()
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
        val response: Response<ResponseBody> = apiServiceMocked.getDataClass("Event").blockingGet()
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
        val response: Response<ResponseBody> = apiServiceMocked.getInfo().blockingGet()
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
            apiServiceMocked.getEntity("Event", "12").blockingGet()
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
            apiServiceMocked.getEntityWithAttributes("Event", "12", "id,title").blockingGet()
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
        val response: Response<ResponseBody> = apiServiceMocked.getEntities("Event").blockingGet()
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
            apiServiceMocked.getEntitiesWithAttributes("Event", "id,title").blockingGet()
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
            apiServiceMocked.getEntitiesFiltered("Event", "\"id>13 AND id<16\"").blockingGet()
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
        val response: Response<ResponseBody> = apiServiceMocked.getEntitiesFilteredWithAttributes(
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

    private fun assertResponseSuccessful(response: Response<*>) {
        assertNotNull(response.body())
        assertEquals(9, response.headers().toHeaderList().size)
        assertEquals(HttpURLConnection.HTTP_OK, response.code())
        assertEquals(true, response.isSuccessful)
        assertEquals(null, response.errorBody())
    }
}
