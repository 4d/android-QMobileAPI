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
import com.qmobile.qmobileapi.model.catalog.Attribute
import com.qmobile.qmobileapi.model.catalog.Catalog
import com.qmobile.qmobileapi.model.info.Info
import com.qmobile.qmobileapi.network.ApiClient
import com.qmobile.qmobileapi.network.ApiService
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.LoginRequiredCallback
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import com.qmobile.qmobileapi.utils.assertRequest
import com.qmobile.qmobileapi.utils.assertResponseSuccessful
import com.qmobile.qmobileapi.utils.mockResponse
import com.qmobile.qmobileapi.utils.parseToType
import junit.framework.TestCase
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
class ApiTest {

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
                    // `test get catalog`()
                    "/\$catalog" -> {
                        mockResponse("restcatalog.json")
                    }
                    // `test get all dataClasses`()
                    "/\$catalog/\$all" -> {
                        mockResponse("restcatalogall.json")
                    }
                    // `test get dataClass`()
                    "/\$catalog/Employee" -> {
                        mockResponse("restdataclass.json")
                    }
                    // `test get info`()
                    "/\$info" -> {
                        mockResponse("restinfo.json")
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
    fun `get catalog`() {
        // Action /$catalog
        val response: Response<ResponseBody> = apiService.getCatalog().blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val catalog = mapper.parseToType<Catalog>(json)
        assertEquals(4, catalog?.dataClasses?.size)
        assertEquals("__DeletedRecords", catalog?.dataClasses?.get(0)?.name)
    }

    @Test
    fun `get all dataClasses`() {
        // Action /$catalog/$all
        val response: Response<ResponseBody> = apiService.getAllDataClasses().blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        TestCase.assertNotNull(json)

        val catalog = mapper.parseToType<Catalog>(json)
        assertEquals(4, catalog?.dataClasses?.size)
        assertEquals("__DeletedRecords", catalog?.dataClasses?.get(0)?.name)
        assertEquals(Attribute.Scope.PUBLIC, catalog?.dataClasses?.get(0)?.scope)
    }

    @Test
    fun `get dataClass`() {
        // Action /$catalog/Employee
        val response: Response<ResponseBody> = apiService.getDataClass("Employee").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        TestCase.assertNotNull(json)

        val catalog = mapper.parseToType<Catalog>(json)
        assertEquals("Employee", catalog?.dataClasses?.get(0)?.name)
        assertEquals("EmployeeSelection", catalog?.dataClasses?.get(0)?.collectionName)
        assertEquals(Attribute.Scope.PUBLIC, catalog?.dataClasses?.get(0)?.scope)
        assertEquals("ID", catalog?.dataClasses?.get(0)?.attributes?.get(0)?.name)
        assertEquals(Attribute.Kind.STORAGE, catalog?.dataClasses?.get(0)?.attributes?.get(0)?.kind)
    }

    @Test
    fun `get info`() {
        // Action /$info
        val response: Response<ResponseBody> = apiService.getInfo().blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val info = mapper.parseToType<Info>(json)
        assertEquals(0, info?.entitySetCount)
        assertEquals(3, info?.ProgressInfo?.size)
        assertEquals("indexProgressIndicator", info?.ProgressInfo?.get(0)?.UserInfo)
        assertEquals("flushProgressIndicator", info?.ProgressInfo?.get(2)?.UserInfo)
    }
}
