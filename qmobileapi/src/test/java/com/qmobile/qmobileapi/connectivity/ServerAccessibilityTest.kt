/*
 * Created by Quentin Marciset on 5/11/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.connectivity

import com.qmobile.qmobileapi.repository.ServerAccessibility
import com.qmobile.qmobileapi.utils.PING_TIMEOUT
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.MalformedURLException
import java.net.URL

class ServerAccessibilityTest {

    private val serverAccessibility = ServerAccessibility()

    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `pingServer ok`() {
        val urlString = "https://localhost:8099"
        val url = URL(urlString)
        serverAccessibility.pingServer(
            hostname = url.host,
            port = url.port,
            timeout = PING_TIMEOUT
        ) { isAccessible, throwable ->
            Assert.assertNull(throwable)
        }
    }

    @Test
    fun `pingServer wrong port`() {
        val urlString = "https://localhost"
        val url = URL(urlString)
        serverAccessibility.pingServer(
            hostname = url.host,
            port = url.port,
            timeout = PING_TIMEOUT
        ) { isAccessible, throwable ->
            Assert.assertTrue(throwable is IllegalArgumentException)
            Assert.assertNull(isAccessible)
        }
    }

    @Test(expected = MalformedURLException::class)
    fun `pingServer wrong url`() {
        val urlString = "localhost"
        val url = URL(urlString)
        serverAccessibility.pingServer(
            hostname = url.host,
            port = url.port,
            timeout = PING_TIMEOUT
        ) { isAccessible, throwable ->
            Assert.fail()
        }
    }
}
