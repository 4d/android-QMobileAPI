/*
 * Created by Quentin Marciset on 29/5/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.auth

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder.Companion.AUTH_APPLICATION
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder.Companion.AUTH_DEVICE
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder.Companion.AUTH_EMAIL
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder.Companion.AUTH_LANGUAGE
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder.Companion.AUTH_PARAMETERS
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder.Companion.AUTH_PASSWORD
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SharedPreferencesHolderTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var sharedPreferencesHolder: SharedPreferencesHolder

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferencesHolder = SharedPreferencesHolder.getInstance(context)
        sharedPreferencesHolder.prefs.edit().clear().apply()
        sharedPreferencesHolder.privatePrefs.edit().clear().apply()
    }

    @Test
    fun appInfo() {
        Assert.assertEquals("{}", sharedPreferencesHolder.appInfo.toString())
        val currentAppInfo = sharedPreferencesHolder.appInfo
        currentAppInfo.put("NEW_KEY", "NEW_VALUE")
        sharedPreferencesHolder.appInfo = currentAppInfo
        val newAppInfo = SharedPreferencesHolder.getInstance(context).appInfo
        Assert.assertEquals("NEW_VALUE", newAppInfo["NEW_KEY"])
    }

    @Test
    fun device() {
        Assert.assertEquals("{}", sharedPreferencesHolder.device.toString())
        val currentDevice = sharedPreferencesHolder.device
        currentDevice.put("NEW_KEY", "NEW_VALUE")
        sharedPreferencesHolder.device = currentDevice
        val newDevice = SharedPreferencesHolder.getInstance(context).device
        Assert.assertEquals("NEW_VALUE", newDevice["NEW_KEY"])
    }

    @Test
    fun team() {
        Assert.assertEquals("{}", sharedPreferencesHolder.team.toString())
        val currentTeam = sharedPreferencesHolder.team
        currentTeam.put("NEW_KEY", "NEW_VALUE")
        sharedPreferencesHolder.team = currentTeam
        val newTeam = SharedPreferencesHolder.getInstance(context).team
        Assert.assertEquals("NEW_VALUE", newTeam["NEW_KEY"])
    }

    @Test
    fun language() {
        Assert.assertEquals("{}", sharedPreferencesHolder.language.toString())
        val currentLanguage = sharedPreferencesHolder.language
        currentLanguage.put("NEW_KEY", "NEW_VALUE")
        sharedPreferencesHolder.language = currentLanguage
        val newLanguage = SharedPreferencesHolder.getInstance(context).language
        Assert.assertEquals("NEW_VALUE", newLanguage["NEW_KEY"])
    }

    @Test
    fun guestLogin() {
        Assert.assertEquals(false, sharedPreferencesHolder.guestLogin)
        sharedPreferencesHolder.guestLogin = true
        Assert.assertEquals(
            true,
            SharedPreferencesHolder.getInstance(context).guestLogin
        )
    }

    @Test
    fun remoteUrl() {
        Assert.assertEquals("", sharedPreferencesHolder.remoteUrl)
        sharedPreferencesHolder.remoteUrl = "localhost"
        Assert.assertEquals(
            "localhost",
            SharedPreferencesHolder.getInstance(context).remoteUrl
        )
    }

    @Test
    fun sessionId() {
        Assert.assertEquals("", sharedPreferencesHolder.sessionId)
        sharedPreferencesHolder.sessionId = "session id"
        Assert.assertEquals(
            "session id",
            SharedPreferencesHolder.getInstance(context).sessionId
        )
    }

    @Test
    fun sessionToken() {
        Assert.assertEquals("", sharedPreferencesHolder.sessionToken)
        sharedPreferencesHolder.sessionToken = "session token"
        Assert.assertEquals(
            "session token",
            SharedPreferencesHolder.getInstance(context).sessionToken
        )
    }

    @Test
    fun globalStamp() {
        Assert.assertEquals(0, sharedPreferencesHolder.globalStamp)
        sharedPreferencesHolder.globalStamp = 49
        Assert.assertEquals(
            49,
            SharedPreferencesHolder.getInstance(context).globalStamp
        )
    }

    @Test
    fun deletedRecordsStamp() {
        Assert.assertEquals(0, sharedPreferencesHolder.deletedRecordsStamp)
        sharedPreferencesHolder.deletedRecordsStamp = 49
        Assert.assertEquals(
            49,
            SharedPreferencesHolder.getInstance(context).deletedRecordsStamp
        )
    }

    @Test
    fun cookie() {
        Assert.assertEquals("", sharedPreferencesHolder.cookies)
        sharedPreferencesHolder.cookies = "sample cookie"
        Assert.assertEquals(
            "sample cookie",
            SharedPreferencesHolder.getInstance(context).cookies
        )
    }

    @Test
    fun buildAuthRequestBody() {
        val email = "a@b.com"
        val password = "123abc"
        val authRequestBody = sharedPreferencesHolder.buildAuthRequestBody(email, password)
        Assert.assertEquals(email, authRequestBody[AUTH_EMAIL])
        Assert.assertEquals(password, authRequestBody[AUTH_PASSWORD])
        Assert.assertNotNull(authRequestBody[AUTH_APPLICATION])
        Assert.assertTrue(authRequestBody[AUTH_APPLICATION] is JSONObject)
        Assert.assertNotNull(authRequestBody[AUTH_DEVICE])
        Assert.assertTrue(authRequestBody[AUTH_DEVICE] is JSONObject)
        Assert.assertNotNull(authRequestBody[AUTH_LANGUAGE])
        Assert.assertTrue(authRequestBody[AUTH_LANGUAGE] is JSONObject)
        Assert.assertNotNull(authRequestBody[AUTH_PARAMETERS])
        Assert.assertTrue(authRequestBody[AUTH_PARAMETERS] is JSONObject)
    }

    @Test
    fun `handleLoginInfo true`() {
        val id = UUID.randomUUID().toString()
        val authResponse = AuthResponse(
            id = id,
            statusText = "",
            success = true,
            token = "token",
            userInfo = mapOf()
        )

        Assert.assertEquals("", sharedPreferencesHolder.sessionId)
        Assert.assertEquals("", sharedPreferencesHolder.sessionToken)
        val success = sharedPreferencesHolder.handleLoginInfo(authResponse)
        Assert.assertTrue(success)
        Assert.assertEquals(
            id,
            SharedPreferencesHolder.getInstance(context).sessionId
        )
        Assert.assertEquals(
            "token",
            SharedPreferencesHolder.getInstance(context).sessionToken
        )
    }

    @Test
    fun `handleLoginInfo false`() {
        val authResponse = AuthResponse(
            id = null,
            statusText = "",
            success = true,
            token = null,
            userInfo = mapOf()
        )

        Assert.assertEquals("", sharedPreferencesHolder.sessionId)
        Assert.assertEquals("", sharedPreferencesHolder.sessionToken)
        val success = sharedPreferencesHolder.handleLoginInfo(authResponse)
        Assert.assertFalse(success)
        Assert.assertEquals(
            "",
            SharedPreferencesHolder.getInstance(context).sessionId
        )
        Assert.assertEquals(
            "",
            SharedPreferencesHolder.getInstance(context).sessionToken
        )
    }
}
