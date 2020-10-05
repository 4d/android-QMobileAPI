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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.qmobile.qmobileapi.auth.AuthInfoHelper.Companion.AUTH_APPLICATION
import com.qmobile.qmobileapi.auth.AuthInfoHelper.Companion.AUTH_DEVICE
import com.qmobile.qmobileapi.auth.AuthInfoHelper.Companion.AUTH_EMAIL
import com.qmobile.qmobileapi.auth.AuthInfoHelper.Companion.AUTH_LANGUAGE
import com.qmobile.qmobileapi.auth.AuthInfoHelper.Companion.AUTH_PARAMETERS
import com.qmobile.qmobileapi.auth.AuthInfoHelper.Companion.AUTH_PASSWORD
import com.qmobile.qmobileapi.auth.AuthInfoHelper.Companion.AUTH_TEAM
import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.model.queries.Queries
import com.qmobile.qmobileapi.model.queries.Query
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
class AuthInfoHelperTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var authInfoHelper: AuthInfoHelper

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        authInfoHelper = AuthInfoHelper.getInstance(context)
        authInfoHelper.prefs.edit().clear().apply()
        authInfoHelper.privatePrefs.edit().clear().apply()
    }

    @Test
    fun testAppInfo() {
        Assert.assertEquals("{}", authInfoHelper.appInfo.toString())
        val currentAppInfo = authInfoHelper.appInfo
        currentAppInfo.put("NEW_KEY", "NEW_VALUE")
        authInfoHelper.appInfo = currentAppInfo
        val newAppInfo = AuthInfoHelper.getInstance(context).appInfo
        Assert.assertEquals("NEW_VALUE", newAppInfo["NEW_KEY"])
    }

    @Test
    fun testDevice() {
        Assert.assertEquals("{}", authInfoHelper.device.toString())
        val currentDevice = authInfoHelper.device
        currentDevice.put("NEW_KEY", "NEW_VALUE")
        authInfoHelper.device = currentDevice
        val newDevice = AuthInfoHelper.getInstance(context).device
        Assert.assertEquals("NEW_VALUE", newDevice["NEW_KEY"])
    }

    @Test
    fun testTeam() {
        Assert.assertEquals("{}", authInfoHelper.team.toString())
        val currentTeam = authInfoHelper.team
        currentTeam.put("NEW_KEY", "NEW_VALUE")
        authInfoHelper.team = currentTeam
        val newTeam = AuthInfoHelper.getInstance(context).team
        Assert.assertEquals("NEW_VALUE", newTeam["NEW_KEY"])
    }

    @Test
    fun testLanguage() {
        Assert.assertEquals("{}", authInfoHelper.language.toString())
        val currentLanguage = authInfoHelper.language
        currentLanguage.put("NEW_KEY", "NEW_VALUE")
        authInfoHelper.language = currentLanguage
        val newLanguage = AuthInfoHelper.getInstance(context).language
        Assert.assertEquals("NEW_VALUE", newLanguage["NEW_KEY"])
    }

    @Test
    fun testGuestLogin() {
        Assert.assertEquals(false, authInfoHelper.guestLogin)
        authInfoHelper.guestLogin = true
        Assert.assertEquals(
            true,
            AuthInfoHelper.getInstance(context).guestLogin
        )
    }

    @Test
    fun testRemoteUrl() {
        Assert.assertEquals("", authInfoHelper.remoteUrl)
        authInfoHelper.remoteUrl = "localhost"
        Assert.assertEquals(
            "localhost",
            AuthInfoHelper.getInstance(context).remoteUrl
        )
    }

    @Test
    fun testDeviceUUID() {
        Assert.assertNotEquals("", authInfoHelper.deviceUUID)
    }

    @Test
    fun testSessionId() {
        Assert.assertEquals("", authInfoHelper.sessionId)
        authInfoHelper.sessionId = "session id"
        Assert.assertEquals(
            "session id",
            AuthInfoHelper.getInstance(context).sessionId
        )
    }

    @Test
    fun testSessionToken() {
        Assert.assertEquals("", authInfoHelper.sessionToken)
        authInfoHelper.sessionToken = "session token"
        Assert.assertEquals(
            "session token",
            AuthInfoHelper.getInstance(context).sessionToken
        )
    }

    @Test
    fun testGlobalStamp() {
        Assert.assertEquals(0, authInfoHelper.globalStamp)
        authInfoHelper.globalStamp = 49
        Assert.assertEquals(
            49,
            AuthInfoHelper.getInstance(context).globalStamp
        )
    }

    @Test
    fun testDeletedRecordsStamp() {
        Assert.assertEquals(0, authInfoHelper.deletedRecordsStamp)
        authInfoHelper.deletedRecordsStamp = 49
        Assert.assertEquals(
            49,
            AuthInfoHelper.getInstance(context).deletedRecordsStamp
        )
    }

    @Test
    fun testCookie() {
        Assert.assertEquals("", authInfoHelper.cookie)
        authInfoHelper.cookie = "sample cookie"
        Assert.assertEquals(
            "sample cookie",
            AuthInfoHelper.getInstance(context).cookie
        )
    }

    @Test
    fun testBuildAuthRequestBody() {
        val email = "a@b.com"
        val password = "123abc"
        val authRequestBody = authInfoHelper.buildAuthRequestBody(email, password)
        Assert.assertEquals(email, authRequestBody[AUTH_EMAIL])
        Assert.assertEquals(password, authRequestBody[AUTH_PASSWORD])
        Assert.assertNotNull(authRequestBody[AUTH_APPLICATION])
        Assert.assertTrue(authRequestBody[AUTH_APPLICATION] is JSONObject)
        Assert.assertNotNull(authRequestBody[AUTH_DEVICE])
        Assert.assertTrue(authRequestBody[AUTH_DEVICE] is JSONObject)
        Assert.assertNotNull(authRequestBody[AUTH_TEAM])
        Assert.assertTrue(authRequestBody[AUTH_TEAM] is JSONObject)
        Assert.assertNotNull(authRequestBody[AUTH_LANGUAGE])
        Assert.assertTrue(authRequestBody[AUTH_LANGUAGE] is JSONObject)
        Assert.assertNotNull(authRequestBody[AUTH_PARAMETERS])
        Assert.assertTrue(authRequestBody[AUTH_PARAMETERS] is JSONObject)
    }

    @Test
    fun testHandleLoginInfoTrue() {

        val id = UUID.randomUUID().toString()
        val authResponse = AuthResponse(
            id = id,
            statusText = "",
            success = true,
            token = "token",
            userInfo = JsonObject()
        )

        Assert.assertEquals("", authInfoHelper.sessionId)
        Assert.assertEquals("", authInfoHelper.sessionToken)
        val success = authInfoHelper.handleLoginInfo(authResponse)
        Assert.assertTrue(success)
        Assert.assertEquals(
            id,
            AuthInfoHelper.getInstance(context).sessionId
        )
        Assert.assertEquals(
            "token",
            AuthInfoHelper.getInstance(context).sessionToken
        )
    }

    @Test
    fun testHandleLoginInfoFalse() {

        val authResponse = AuthResponse(
            id = null,
            statusText = "",
            success = true,
            token = null,
            userInfo = JsonObject()
        )

        Assert.assertEquals("", authInfoHelper.sessionId)
        Assert.assertEquals("", authInfoHelper.sessionToken)
        val success = authInfoHelper.handleLoginInfo(authResponse)
        Assert.assertFalse(success)
        Assert.assertEquals(
            "",
            AuthInfoHelper.getInstance(context).sessionId
        )
        Assert.assertEquals(
            "",
            AuthInfoHelper.getInstance(context).sessionToken
        )
    }

    @Test
    fun testQueries() {
        val queryList = mutableListOf<Query>()
        queryList.add(Query("Employee", "name=John"))
        queryList.add(Query("Office", "location=Berlin"))
        queryList.add(Query("Service", "numberOfEmployees>50"))
        val queriesObject = Queries(queryList)

        for (query in queriesObject.queries) {
            query.tableName?.let {
                Assert.assertEquals("", authInfoHelper.getQuery(it))
            } ?: kotlin.run { Assert.fail() }
        }

        val queriesJson = Gson().toJson(queriesObject)
        val queriesJSONObject = JSONObject(queriesJson)
        authInfoHelper.setQueries(queriesJSONObject)

        Assert.assertEquals(
            "name=John",
            AuthInfoHelper.getInstance(context).getQuery("Employee")
        )
        Assert.assertEquals(
            "location=Berlin",
            AuthInfoHelper.getInstance(context).getQuery("Office")
        )
        Assert.assertEquals(
            "numberOfEmployees>50",
            AuthInfoHelper.getInstance(context).getQuery("Service")
        )
    }
}
