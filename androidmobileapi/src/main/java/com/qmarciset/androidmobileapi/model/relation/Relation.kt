/*
 * Created by Quentin Marciset on 24/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.relation

data class Relations(
    val relations: List<Relation>
)

data class Relation(
    val name: String?,
    val tableName: String?,
    val type: String?
)
