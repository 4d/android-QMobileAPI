/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.error

@Suppress("ConstructorParameterNaming")
data class ErrorResponse(
    val success: Boolean?,
    val __ERRORS: List<ErrorReason>?
)
