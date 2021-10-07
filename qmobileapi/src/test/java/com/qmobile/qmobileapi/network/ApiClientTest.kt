/*
 * Created by Quentin Marciset on 4/11/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.network

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.qmobile.qmobileapi.auth.LoginRequiredCallback
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ApiClientTest {

    @Mock
    lateinit var loginRequiredCallbackMock: LoginRequiredCallback

    @Mock
    lateinit var loginApiServiceMock: LoginApiService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        ApiClient.clearApiClients()
    }

    @Test
    fun `getLoginApiService success`() {
        val loginApiService =
            ApiClient.getLoginApiService("", SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext()))
        Assert.assertNotNull(loginApiService)
        val anotherLoginApiService =
            ApiClient.getLoginApiService("http://anything", SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext()))
        Assert.assertNotNull(anotherLoginApiService)
        Assert.assertTrue(loginApiService === anotherLoginApiService)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getLoginApiService fail`() {
        val loginApiService =
            ApiClient.getLoginApiService("localhost", SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext()))
        Assert.fail()
    }

    @Test
    fun `getApiService success`() {
        Assert.assertNull(ApiClient.retrofit)
        val apiService = ApiClient.getApiService(
            baseUrl = "https://anything",
            loginApiService = loginApiServiceMock,
            loginRequiredCallback = loginRequiredCallbackMock,
            sharedPreferencesHolder = SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext())
        )
        Assert.assertNotNull(apiService)
        Assert.assertNotNull(ApiClient.retrofit)
        val saveRetrofitRef = ApiClient.retrofit
        val anotherApiService = ApiClient.getApiService(
            baseUrl = "",
            loginApiService = loginApiServiceMock,
            loginRequiredCallback = loginRequiredCallbackMock,
            sharedPreferencesHolder = SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext())
        )
        Assert.assertNotNull(anotherApiService)
        Assert.assertTrue(apiService === anotherApiService)
        Assert.assertNotNull(ApiClient.retrofit)
        Assert.assertTrue(saveRetrofitRef === ApiClient.retrofit)
    }

    @Test
    fun `volatile variable`() {
        val loginApiServiceInstance = ApiClient.LOGIN_INSTANCE
        Assert.assertNull(loginApiServiceInstance)
        val loginApiService =
            ApiClient.getLoginApiService("", SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext()))

        val loginApiServiceInstance1 = ApiClient.LOGIN_INSTANCE
        Assert.assertNotNull(loginApiServiceInstance1)

        val anotherLoginApiService =
            ApiClient.getLoginApiService("http://anything", SharedPreferencesHolder.getInstance(ApplicationProvider.getApplicationContext()))

        val loginApiServiceInstance2 = ApiClient.LOGIN_INSTANCE
        Assert.assertNotNull(loginApiServiceInstance2)

        Assert.assertFalse(loginApiServiceInstance === loginApiServiceInstance1)
        Assert.assertTrue(loginApiServiceInstance1 === loginApiServiceInstance2)
    }

    @Test
    fun buildUrl() {
        var url = ApiClient.buildUrl("localhost")
        Assert.assertEquals("https://localhost/mobileapp/", url)
        url = ApiClient.buildUrl("localhost/")
        Assert.assertEquals("https://localhost/mobileapp/", url)
        url = ApiClient.buildUrl("http://localhost/")
        Assert.assertEquals("http://localhost/mobileapp/", url)
        url = ApiClient.buildUrl("https://localhost/")
        Assert.assertEquals("https://localhost/mobileapp/", url)
        url = ApiClient.buildUrl("")
        Assert.assertEquals("https://mobileapp/", url)
        url = ApiClient.buildUrl("/")
        Assert.assertEquals("https://mobileapp/", url)
    }
}
