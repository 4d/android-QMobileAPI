/*
 * Created by Quentin Marciset on 3/11/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.os.Build
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class JSONExtTest {

    @Test
    fun getSafeObject() {
        val subJsonObject = JSONObject().apply { put("property_1", "value_1") }
        val jsonObject = JSONObject().apply { put("subJson", subJsonObject) }
        val extracted = jsonObject.getSafeObject("subJson")
        Assert.assertNotNull(extracted)
        Assert.assertEquals("value_1", extracted?.get("property_1"))
    }

    @Test
    fun `getSafeObject null`() {
        val subJsonObject = JSONObject().apply { put("property_1", "value_1") }
        val jsonObject = JSONObject().apply { put("subJson", subJsonObject) }
        val extracted = jsonObject.getSafeObject("wrongEntry")
        Assert.assertNull(extracted)
    }

    @Test
    fun getSafeArray() {
        val subJsonObject = JSONObject().apply { put("property_1", "value_1") }
        val subArray = JSONArray().apply {
            put("thisString")
            put(5)
            put(true)
            put(subJsonObject)
        }
        val jsonObject = JSONObject().apply { put("sub", subArray) }
        val extracted = jsonObject.getSafeArray("sub")
        Assert.assertNotNull(extracted)
        val stringList = extracted.getStringList()
        Assert.assertNotEquals(0, stringList.size)
        Assert.assertEquals("thisString", stringList[0])
    }

    @Test
    fun `getSafe null`() {
        val subArray = JSONArray().apply {
            put("thisString")
        }
        val jsonObject = JSONObject().apply { put("sub", subArray) }
        val extracted = jsonObject.getSafeArray("wrongEntry")
        Assert.assertNull(extracted)
    }

    @Test
    fun getSafeString() {
        val jsonObject = JSONObject().apply { put("subString", "thisString") }
        val extracted = jsonObject.getSafeString("subString")
        Assert.assertNotNull(extracted)
        Assert.assertEquals("thisString", extracted)
    }

    @Test
    fun `getSafeString null`() {
        val jsonObject = JSONObject().apply { put("subJson", "thisString") }
        val extracted = jsonObject.getSafeString("wrongEntry")
        Assert.assertNull(extracted)
    }

    @Test
    fun getSafeInt() {
        val jsonObject = JSONObject().apply { put("subString", 5) }
        val extracted = jsonObject.getSafeInt("subString")
        Assert.assertNotNull(extracted)
        Assert.assertEquals(5, extracted)
    }

    @Test
    fun `getSafeInt null`() {
        val jsonObject = JSONObject().apply { put("subJson", 5) }
        val extracted = jsonObject.getSafeInt("wrongEntry")
        Assert.assertNull(extracted)
    }

    @Test
    fun getSafeBoolean() {
        val jsonObject = JSONObject().apply { put("subString", false) }
        val extracted = jsonObject.getSafeBoolean("subString")
        Assert.assertNotNull(extracted)
        Assert.assertEquals(false, extracted)
    }

    @Test
    fun `getSafeBoolean null`() {
        val jsonObject = JSONObject().apply { put("subJson", false) }
        val extracted = jsonObject.getSafeBoolean("wrongEntry")
        Assert.assertNull(extracted)
    }

    @Test
    fun `wrong type null`() {
        val jsonObject = JSONObject().apply { put("subJson", 5) }
        val extracted = jsonObject.getSafeBoolean("subJson")
        Assert.assertNull(extracted)
    }
}
