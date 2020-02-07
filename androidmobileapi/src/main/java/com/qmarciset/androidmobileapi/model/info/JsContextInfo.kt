/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.info

data class JsContextInfo(
    val activeDebugger: Boolean? = null,
    val contextPoolSize: Int? = null,
    val createdContextCount: Int? = null,
    val destroyedContextCount: Int? = null,
    val reusableContextCount: Int? = null,
    val unusedContextCount: Int? = null,
    val usedContextCount: Int? = null,
    val usedContextMaxCount: Int? = null
)
