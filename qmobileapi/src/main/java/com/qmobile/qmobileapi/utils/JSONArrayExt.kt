/*
 * Created by Quentin Marciset on 4/6/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun JSONArray?.getStringList(): List<String> {
    val list = mutableListOf<String>()
    this?.let {
        for (i in 0 until this.length()) {
            this.getSafeString(i)?.let { list.add(it) }
        }
    }
    return list
}

fun JSONArray.getSafeString(position: Int): String? {
    return try {
        this.getString(position)
    } catch (e: JSONException) {
        null
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

fun JSONArray?.getJSONObjectList(): List<JSONObject> {
    val list = mutableListOf<JSONObject>()
    this?.let {
        for (i in 0 until this.length()) {
            val safeObject = this.getSafeObject(i)
            safeObject?.let { list.add(it) }
        }
    }
    return list
}

fun JSONArray.getSafeObject(position: Int): JSONObject? {
    return try {
        this.getJSONObject(position)
    } catch (e: JSONException) {
        null
    }
}
