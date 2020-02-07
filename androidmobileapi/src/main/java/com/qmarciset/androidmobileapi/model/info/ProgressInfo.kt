/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.info

@Suppress("ConstructorParameterNaming")
data class ProgressInfo(
    val UserInfo: String? = null,
    val percent: Int,
    val sessions: Int? = null
)
