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
import com.qmarciset.androidmobileapi.model.info.Info
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.ApiService
import com.qmarciset.androidmobileapi.network.LoginApiService
import com.qmarciset.androidmobileapi.utils.assertRequest
import com.qmarciset.androidmobileapi.utils.assertResponseSuccessful
import com.qmarciset.androidmobileapi.utils.mockResponse
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
    fun `test get catalog`() {
        // Action /$catalog
        val response: Response<ResponseBody> = apiService.getCatalog().blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        assertNotNull(json)

        val catalog = gson.parseJsonToType<Catalog>(json)
        assertEquals(4, catalog?.dataClasses?.size)
        assertEquals("__DeletedRecords", catalog?.dataClasses?.get(0)?.name)
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
        assertEquals(4, catalog?.dataClasses?.size)
        assertEquals("__DeletedRecords", catalog?.dataClasses?.get(0)?.name)
        assertEquals(Scope.PUBLIC, catalog?.dataClasses?.get(0)?.scope)
    }

    @Test
    fun `test get dataClass`() {
        // Action /$catalog/Employee
        val response: Response<ResponseBody> = apiService.getDataClass("Employee").blockingGet()
        assertResponseSuccessful(response)

        val responseBody = response.body()
        val json = responseBody?.string()
        TestCase.assertNotNull(json)

        val catalog = gson.parseJsonToType<Catalog>(json)
        assertEquals("Employee", catalog?.dataClasses?.get(0)?.name)
        assertEquals("EmployeeSelection", catalog?.dataClasses?.get(0)?.collectionName)
        assertEquals(Scope.PUBLIC, catalog?.dataClasses?.get(0)?.scope)
        assertEquals("ID", catalog?.dataClasses?.get(0)?.attributes?.get(0)?.name)
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
        assertEquals(0, info?.entitySetCount)
        assertEquals(3, info?.ProgressInfo?.size)
        assertEquals("indexProgressIndicator", info?.ProgressInfo?.get(0)?.UserInfo)
        assertEquals("flushProgressIndicator", info?.ProgressInfo?.get(2)?.UserInfo)
    }
}
