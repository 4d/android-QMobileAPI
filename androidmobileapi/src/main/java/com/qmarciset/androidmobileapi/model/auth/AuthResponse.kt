/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.auth

import com.google.gson.JsonObject

@Suppress("ConstructorParameterNaming")
data class AuthResponse(
    val id: String?,
    val statusText: String?,
    val success: Boolean,
    val token: String?,
    val userInfo: JsonObject?
)
