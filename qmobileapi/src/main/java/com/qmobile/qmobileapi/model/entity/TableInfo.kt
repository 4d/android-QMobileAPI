/*
 * Created by qmarciset on 9/6/2022.
 * 4D SAS
 * Copyright (c) 2022 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.entity

data class TableInfo(
    val originalName: String,
    val query: String,
    val fields: Map<String, String>, // <field.name.fieldAdjustment(), field.name>
    val searchFields: List<String>,
    val searchableWithBarcode: Boolean
) {
    fun hasUserQuery(): Boolean = query.contains(":")
}
