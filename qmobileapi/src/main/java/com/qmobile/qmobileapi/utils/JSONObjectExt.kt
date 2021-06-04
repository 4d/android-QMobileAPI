/*
 * Created by Quentin Marciset on 4/6/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun JSONObject.getSafeObject(key: String): JSONObject? {
    return try {
        this.getJSONObject(key)
    } catch (e: JSONException) {
        return null
    }
}

fun JSONObject.getSafeString(key: String): String? {
    return try {
        this.getString(key)
    } catch (e: JSONException) {
        return null
    }
}

fun JSONObject.getSafeInt(key: String): Int? {
    return try {
        this.getInt(key)
    } catch (e: JSONException) {
        return null
    }
}

fun JSONObject.getSafeBoolean(key: String): Boolean? {
    return try {
        this.getBoolean(key)
    } catch (e: JSONException) {
        return null
    }
}

fun JSONObject.getSafeArray(key: String): JSONArray? {
    return try {
        this.getJSONArray(key)
    } catch (e: JSONException) {
        return null
    }
}

fun JSONObject.toStringMap(): Map<String, String> {
    val map: MutableMap<String, String> = mutableMapOf()

    this.names()?.let { keysIndex ->
        for (i in 0 until keysIndex.length()) {
            val key = keysIndex.getString(i)
            this.getSafeString(key)?.let { value ->
                map[key] = value
            }
        }
    }
    return map
}
