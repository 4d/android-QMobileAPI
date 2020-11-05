/*
 * Created by Quentin Marciset on 4/11/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.repository

import android.os.Build
import com.qmobile.qmobileapi.network.ApiService
import com.qmobile.qmobileapi.utils.any
import com.qmobile.qmobileapi.utils.buildSampleResponseFromJsonString
import com.qmobile.qmobileapi.utils.employeeEntitiesString
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.hamcrest.core.IsInstanceOf.instanceOf
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
import java.util.concurrent.CancellationException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RestRepositoryTest {

    private val predicate = "\"__GlobalStamp >= 0\""
    private val tableName = "Employee"

    lateinit var restRepository: RestRepository

    @Mock
    lateinit var mockedApiService: ApiService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        restRepository = RestRepository(tableName, mockedApiService)
    }

    @Test
    fun `getEntities success`() {

        val response = buildSampleResponseFromJsonString(employeeEntitiesString)

        Mockito.`when`(
            mockedApiService.getEntities(
                dataClassName = tableName,
                filter = predicate,
                attributes = null
            )
        ).thenAnswer { Single.just(response) }

        restRepository.getEntities(
            filter = predicate
        ) { isSuccess, res, error ->
            Assert.assertNull(error)
            Assert.assertTrue(isSuccess)
            Assert.assertNotNull(res?.body()?.string())
        }
    }

    @Test
    fun getEntitiesExtendedAttributes() {

        val response = buildSampleResponseFromJsonString(employeeEntitiesString)

        val jsonRequestBody = JSONObject()

        Mockito.`when`(
            mockedApiService.getEntitiesExtendedAttributes(
                body = any(RequestBody::class.java),
                dataClassName = Mockito.anyString(),
                filter = Mockito.anyString(),
                extendedAttributes = Mockito.anyBoolean(),
                limit = Mockito.anyInt()
            )
        ).thenAnswer { Single.just(response) }

        restRepository.getEntitiesExtendedAttributes(
            jsonRequestBody = jsonRequestBody,
            filter = predicate
        ) { isSuccess, res, error ->
            Assert.assertNull(error)
            Assert.assertTrue(isSuccess)
            Assert.assertNotNull(res?.body()?.string())
        }
    }

    @Test
    fun `getEntities fail`() {

        Mockito.`when`(
            mockedApiService.getEntities(
                dataClassName = tableName,
                filter = predicate,
                attributes = null
            )
        ).thenAnswer { Single.error<CancellationException>(CancellationException()) }

        restRepository.getEntities(
            filter = predicate
        ) { isSuccess, res, error ->
            Assert.assertNotNull(error)
            Assert.assertThat(error, instanceOf(CancellationException::class.java))
            Assert.assertFalse(isSuccess)
            Assert.assertNull(res?.body()?.string())
        }
    }
}
