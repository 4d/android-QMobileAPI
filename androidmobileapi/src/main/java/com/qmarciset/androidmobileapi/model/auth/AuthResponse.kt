package com.qmarciset.androidmobileapi.model.auth

@Suppress("ConstructorParameterNaming")
data class AuthResponse(
    val id: String?,
    val statusText: String?,
    val success: Boolean,
    val token: String?,
    val userInfo: UserInfo?
)
