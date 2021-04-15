/*
 * Created by Quentin Marciset on 4/11/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SharedPreferencesExtTest {

    @Test
    fun `preferences creation`() {
        val defPref = defaultPrefs(ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(defPref)
        val customPref = customPrefs(ApplicationProvider.getApplicationContext(), "custom")
        Assert.assertNotNull(customPref)

        defPref["key"] = "value"

        Assert.assertEquals("value", defPref.get<String>("key"))
        Assert.assertNull(customPref.get<String>("key"))
    }

    @Test
    fun `get string`() {
        val defPref = defaultPrefs(ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(defPref)

        defPref["key"] = "value"

        Assert.assertEquals("value", defPref.get<String>("key"))
        Assert.assertNull(defPref.get<String>("wrongKey"))
    }

    @Test
    fun `get int`() {
        val defPref = defaultPrefs(ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(defPref)

        defPref["key"] = 5

        Assert.assertEquals(5, defPref.get<Int>("key"))
        Assert.assertEquals(0, defPref.get<Int>("wrongKey"))
    }

    @Test
    fun `get float`() {
        val defPref = defaultPrefs(ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(defPref)

        defPref["key"] = 5f

        Assert.assertEquals(5f, defPref.get<Float>("key"))
        Assert.assertEquals(-1f, defPref.get<Float>("wrongKey"))
    }

    @Test
    fun `get long`() {
        val defPref = defaultPrefs(ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(defPref)

        defPref["key"] = 5L

        Assert.assertEquals(5L, defPref.get<Long>("key"))
        Assert.assertEquals(-1L, defPref.get<Long>("wrongKey"))
    }

    @Test
    fun `get boolean`() {
        val defPref = defaultPrefs(ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(defPref)

        defPref["key"] = true

        Assert.assertEquals(true, defPref.get<Boolean>("key"))
        Assert.assertEquals(false, defPref.get<Boolean>("wrongKey"))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `get exception`() {
        val defPref = defaultPrefs(ApplicationProvider.getApplicationContext())
        Assert.assertNotNull(defPref)

        defPref["key"] = JSONObject()

        val exception = defPref.get<JSONObject>("key")
        Assert.fail()
    }
}
