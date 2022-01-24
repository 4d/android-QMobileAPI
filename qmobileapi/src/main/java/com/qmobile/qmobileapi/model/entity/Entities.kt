/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.entity

data class Entities<T : EntityModel>(
    val __COUNT: Int?,
    val __GlobalStamp: Int?,
    val __ENTITIES: List<T>?,
    val __FIRST: Int?,
    val __SENT: Int?,
    val __DATACLASS: String?,
    val __entityModel: String?, // filled in Entities response
    val __ENTITYSET: String? // filled in one-to-many relations
)
