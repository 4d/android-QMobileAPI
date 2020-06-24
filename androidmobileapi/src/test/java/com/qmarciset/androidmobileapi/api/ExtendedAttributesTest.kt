/*
 * Created by Quentin Marciset on 23/6/2020.
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
import com.qmarciset.androidmobileapi.utils.APP_JSON
import com.qmarciset.androidmobileapi.utils.EmployeeExtendedAttributes
import com.qmarciset.androidmobileapi.utils.ServiceExtendedAttributes
import com.qmarciset.androidmobileapi.utils.UTF8_CHARSET
import com.qmarciset.androidmobileapi.utils.assertRequest
import com.qmarciset.androidmobileapi.utils.assertResponseSuccessful
import com.qmarciset.androidmobileapi.utils.mockResponse
import com.qmarciset.androidmobileapi.utils.parseJsonToType
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

                    // `test extendedAttributes true or false is the same`()
                    "/Service?\$filter=%22name%3DSponsorship%22&\$extendedAttributes=true" -> {
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
    fun `test extendedAttributes`() {
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

        val entities = gson.parseJsonToType<Entities>(json)
        assertEquals("Service", entities?.__entityModel)

        val services = gson.parseJsonToType<List<ServiceExtendedAttributes>>(entities?.__ENTITIES)
        assertEquals(1, services?.size)
        val service = services?.get(0)
        assertNull(service?.employeeNumber)
        assertNotNull(service?.manager)
        assertNotNull(service?.employees)

        val manager = service?.manager
        assertEquals("Joel", manager?.FirstName)
        assertEquals("Price", manager?.LastName)
        assertEquals("Klean Corp", manager?.Company)
        assertNotNull(manager?.Location)
        assertNotNull(manager?.Notes)

        val employees = gson.parseJsonToType<List<EmployeeExtendedAttributes>>(service?.employees?.__ENTITIES)
        assertEquals(1, employees?.size)
        val employee = employees?.get(0)

        assertEquals("Joel", employee?.FirstName)
        assertNull(employee?.LastName)
        assertNull(employee?.Job)
        assertNull(employee?.Company)
        assertNull(employee?.Address)
    }
}
