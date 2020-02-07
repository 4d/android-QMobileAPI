/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.entity

import com.google.gson.JsonArray

@Suppress("ConstructorParameterNaming")
data class Entities(
    val __COUNT: Int?,
    val __GlobalStamp: Int?,
    val __ENTITIES: JsonArray?,
    val __FIRST: Int?,
    val __SENT: Int?,
    val __entityModel: String?
)
