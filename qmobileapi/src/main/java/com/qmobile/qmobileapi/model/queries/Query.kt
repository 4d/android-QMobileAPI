/*
 * Created by Quentin Marciset on 11/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.queries

import com.fasterxml.jackson.databind.ObjectMapper
import com.qmobile.qmobileapi.utils.getObjectListAsString
import com.qmobile.qmobileapi.utils.getSafeArray
import com.qmobile.qmobileapi.utils.parseToType
import org.json.JSONObject

data class Query(
    val tableName: String?,
    val value: String?
) {
    companion object {
        const val QUERY_PROPERTY = "__Query"
        const val QUERY_STRING_PROPERTY = "queryString"
        const val SETTINGS = "settings"
        const val PARAMETERS = "parameters"
        const val QUERY_PREFIX = "queries"

        fun buildQueries(mapper: ObjectMapper, queryJsonObj: JSONObject): Map<String, String> {
            val map = mutableMapOf<String, String>()
            queryJsonObj.getSafeArray("queries")?.getObjectListAsString()?.forEach { queryString ->
                mapper.parseToType<Query>(queryString)?.let { query ->
                    if (!query.tableName.isNullOrEmpty() && !query.value.isNullOrEmpty())
                        map["${QUERY_PREFIX}_${query.tableName}"] = query.value
                }
            }
            return map
        }
    }
}
