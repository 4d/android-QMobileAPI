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
        null
    }
}

fun JSONObject.getSafeString(key: String): String? {
    return try {
        this.getString(key)
    } catch (e: JSONException) {
        null
    }
}

fun JSONObject.getSafeInt(key: String): Int? {
    return try {
        this.getInt(key)
    } catch (e: JSONException) {
        null
    }
}

fun JSONObject.getSafeBoolean(key: String): Boolean? {
    return try {
        this.getBoolean(key)
    } catch (e: JSONException) {
        null
    }
}

fun JSONObject.getSafeArray(key: String): JSONArray? {
    return try {
        this.getJSONArray(key)
    } catch (e: JSONException) {
        null
    }
}

fun JSONObject.toStringMap(): Map<String, Any> {
    val map: MutableMap<String, Any> = mutableMapOf()

    this.keys().forEach { key ->
        this.getSafeAny(key)?.let { value ->
            map[key] = value
        }
    }
    return map
}

fun JSONObject.getSafeAny(key: String): Any? {
    return try {
        this.get(key)
    } catch (e: JSONException) {
        null
    }
}
