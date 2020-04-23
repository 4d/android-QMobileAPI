/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.entity

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.qmarciset.androidmobileapi.utils.parseJsonToType
import okhttp3.ResponseBody

@Suppress("ConstructorParameterNaming")
data class Entities(
    val __COUNT: Int?,
    val __GlobalStamp: Int?,
    val __ENTITIES: JsonArray?,
    val __FIRST: Int?,
    val __SENT: Int?,
    val __entityModel: String?
) {

    companion object {
        /**
         * Retrieves data from response
         */
        fun decodeEntities(
            gson: Gson,
            responseBody: ResponseBody,
            onResult: (entities: Entities?) -> Unit
        ) {
            val json = responseBody.string()
            val entities = parseEntities(gson, json)
            onResult(entities)
        }

        /**
         * Parse Entities from json String
         */
        fun parseEntities(gson: Gson, json: String): Entities? {
            return gson.parseJsonToType<Entities>(json)
        }
    }
}
