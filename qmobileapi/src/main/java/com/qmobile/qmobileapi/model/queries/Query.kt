/*
 * Created by Quentin Marciset on 11/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.queries

data class Queries(
    val queries: List<Query>
)

data class Query(
    val tableName: String?,
    val value: String?
) {
    companion object {
        const val QUERY_PROPERTY = "__Query"
        const val QUERY_STRING_PROPERTY = "queryString"
        const val SETTINGS = "settings"
        const val PARAMETERS = "parameters"
    }
}
