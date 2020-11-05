/*
 * Created by Quentin Marciset on 4/11/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.repository

import android.os.Build
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.any
import com.qmobile.qmobileapi.utils.buildSampleResponseFromJsonString
import com.qmobile.qmobileapi.utils.employeeEntitiesString
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.hamcrest.core.IsInstanceOf
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeoutException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AuthRepositoryTest {

    lateinit var authRepository: AuthRepository

    @Mock
    lateinit var mockedApiService: LoginApiService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        authRepository = AuthRepository(mockedApiService)
    }

    @Test
    fun `authenticate success`() {

        val response = buildSampleResponseFromJsonString(employeeEntitiesString)

        val jsonRequestBody = JSONObject()

        Mockito.`when`(
            mockedApiService.authenticate(
                body = any(RequestBody::class.java)
            )
        ).thenAnswer { Single.just(response) }

        authRepository.authenticate(
            jsonRequestBody = jsonRequestBody,
            shouldRetryOnError = false
        ) { isSuccess, res, error ->
            Assert.assertNull(error)
            Assert.assertTrue(isSuccess)
            Assert.assertNotNull(res?.body()?.string())
        }
    }

    @Test
    fun `authenticate fails without retry`() {

        val jsonRequestBody = JSONObject()

        Mockito.`when`(
            mockedApiService.authenticate(
                body = any(RequestBody::class.java)
            )
        ).thenAnswer { Single.error<TimeoutException>(TimeoutException()) }

        authRepository.authenticate(
            jsonRequestBody = jsonRequestBody,
            shouldRetryOnError = false
        ) { isSuccess, res, error ->
            Assert.assertNotNull(error)
            Assert.assertThat(error, IsInstanceOf.instanceOf(TimeoutException::class.java))
            Assert.assertFalse(isSuccess)
            Assert.assertNull(res?.body()?.string())
        }
    }

    @Test
    fun `authenticate fails with retry`() {

        val jsonRequestBody = JSONObject()

        Mockito.`when`(
            mockedApiService.authenticate(
                body = any(RequestBody::class.java)
            )
        ).thenAnswer { Single.error<TimeoutException>(TimeoutException()) }

        authRepository.authenticate(
            jsonRequestBody = jsonRequestBody,
            shouldRetryOnError = true
        ) { isSuccess, res, error ->
            Assert.assertNotNull(error)
            Assert.assertThat(error, IsInstanceOf.instanceOf(TimeoutException::class.java))
            Assert.assertFalse(isSuccess)
            Assert.assertNull(res?.body()?.string())
        }
    }

    @Test
    fun `logout success`() {

        val response = buildSampleResponseFromJsonString(employeeEntitiesString)

        Mockito.`when`(
            mockedApiService.logout()
        ).thenAnswer { Single.just(response) }

        authRepository.logout { isSuccess, res, error ->
            Assert.assertNull(error)
            Assert.assertTrue(isSuccess)
            Assert.assertNotNull(res?.body()?.string())
        }
    }
}
