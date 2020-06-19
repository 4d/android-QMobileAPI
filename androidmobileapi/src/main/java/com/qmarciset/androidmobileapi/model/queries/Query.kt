/*
 * Created by Quentin Marciset on 11/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.queries

data class Queries(
    val queries: List<Query>
)

data class Query(
    val tableName: String?,
    val value: String?
)