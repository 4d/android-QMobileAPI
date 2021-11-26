/*
 * Created by Quentin Marciset on 24/9/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import org.json.JSONObject

fun retrieveJSONObject(jsonString: String): JSONObject? {
    return try {
        JSONObject(jsonString.substring(jsonString.indexOf("{"), jsonString.lastIndexOf("}") + 1))
    } catch (e: StringIndexOutOfBoundsException) {
        null
    }
}

fun String.extractJSON(): String? {
    val start = this.indexOf("{")
    val end = this.lastIndexOf("}")
    if (start >= 0 && end >= 0) {
        return this.substring(start, end + 1)
    }
    return null
}
