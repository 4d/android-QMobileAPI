/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.info

@Suppress("ConstructorParameterNaming")
data class Info(
    val entitySetCount: Int = 0,
    val ProgressInfo: List<ProgressInfo>? = null,
    val cacheSize: Int? = null,
    val entitySet: List<EntitySet>,
    val jsContextInfo: List<JsContextInfo>? = null,
    val sessionInfo: List<SessionInfo>? = null,
    val usedCache: Int? = null
)