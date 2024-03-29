/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.error

data class ErrorReason(
    val message: String?,
    val componentSignature: String?,
    val errCode: Int?
)
