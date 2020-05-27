/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.catalog

data class DataClass(
    val name: String?,
    val dataURI: String?,
    val uri: String?,
    val scope: Scope?,
    val attributes: List<Attribute?>?,
    val className: String?,
    val collectionName: String?,
    val key: List<Key?>?,
    val tableNumber: Int?
)
