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
