/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.entity

import com.google.gson.Gson
import com.qmarciset.androidmobileapi.utils.parseJsonToType
import okhttp3.ResponseBody

@Suppress("ConstructorParameterNaming")
data class Entities<T : EntityModel>(
    val __COUNT: Int?,
    val __GlobalStamp: Int?,
    val __ENTITIES: List<T>?,
    val __FIRST: Int?,
    val __SENT: Int?,
    val __DATACLASS: String?,
    val __entityModel: String?, // filled in Entities response
    val __ENTITYSET: String? // filled in one-to-many relations
) {

    companion object {
        /**
         * Retrieves data from response, retrieve Entities object
         */
        fun <T : EntityModel> decodeEntities(
            gson: Gson,
            responseBody: ResponseBody,
            onResult: (entities: Entities<T>?) -> Unit
        ) {
            val json = responseBody.string()
            val entities: Entities<T>? = parseEntities(gson, json)
            onResult(entities)
        }

        /**
         * Parse Entities from json String
         */
        fun <T : EntityModel> parseEntities(gson: Gson, json: String): Entities<T>? {
            return gson.parseJsonToType(json)
        }
    }
}
