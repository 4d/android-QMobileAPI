package com.qmarciset.androidmobileapi.model.error

@Suppress("ConstructorParameterNaming")
data class ErrorResponse(
    val success: Boolean?,
    val __ERRORS: List<ErrorReason>?
)
