/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.info

data class EntitySet(
    val expires: String? = null,
    val id: String,
    val refreshed: String? = null,
    val selectionSize: Int? = null,
    val sorted: Boolean? = null,
    val tableName: String? = null
)
