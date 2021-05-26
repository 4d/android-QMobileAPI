/*
 * Created by Quentin Marciset on 24/9/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
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

fun JSONArray?.getStringList(): List<String> {
    val list = mutableListOf<String>()
    this?.let {
        for (i in 0 until this.length()) {
            list.add(this.getString(i).toString())
        }
    }
    return list
}

fun JSONArray.getSafeString(position: Int): String? {
    return try {
        this.getString(position)
    } catch (e: JSONException) {
        return null
    }
}

fun JSONArray?.getObjectListAsString(): List<String> {
    val list = mutableListOf<String>()
    this?.let {
        for (i in 0 until this.length()) {
            val safeObject = this.getSafeObject(i)
            list.add(safeObject.toString())
        }
    }
    return list
}

fun JSONArray.getSafeObject(position: Int): JSONObject? {
    return try {
        this.getJSONObject(position)
    } catch (e: JSONException) {
        return null
    }
}

fun retrieveJSONObject(jsonString: String): JSONObject? {
    return try {
        JSONObject(jsonString.substring(jsonString.indexOf("{"), jsonString.lastIndexOf("}") + 1))
    } catch (e: StringIndexOutOfBoundsException) {
        null
    }
}
