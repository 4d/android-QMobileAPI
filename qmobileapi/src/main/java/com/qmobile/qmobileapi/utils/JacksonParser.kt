/*
 * Created by qmarciset on 8/10/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.JsonSyntaxException
import timber.log.Timber

/**
 * Utility method to parse a Json string to type
 */
inline fun <reified T : Any> ObjectMapper.parseToType(json: String?): T? {
    if (json.isNullOrEmpty())
        return null
    return this.readValue<T>(json)
}

/**
 * Utility method to parse an object to Json string
 */
fun <T> ObjectMapper.parseToString(entity: T): String =
    if (entity == null) "" else this.writeValueAsString(entity)

/**
 * Decode action response
 */
inline fun <reified T : Any> retrieveResponseObject(mapper: ObjectMapper, jsonString: String): T? {
    jsonString.extractJSON()?.let {
        return try {
            mapper.parseToType(it)
        } catch (e: JsonSyntaxException) {
            Timber.w("Failed to decode action response ${e.localizedMessage}: $jsonString")
            null
        }
    }
    return null
}
