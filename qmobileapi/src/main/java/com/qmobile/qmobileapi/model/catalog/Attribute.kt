/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.catalog

data class Attribute(
    val identifying: Boolean?,
    val indexed: Boolean?,
    val kind: KindEnum?,
    val name: String?,
    val scope: ScopeEnum?,
    val simpleDate: Boolean?,
    val type: String?
)
