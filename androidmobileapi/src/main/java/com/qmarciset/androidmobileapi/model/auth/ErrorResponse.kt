package com.qmarciset.androidmobileapi.model.auth

data class ErrorResponse(
    val message: String?,
    val componentSignature: String?,
    val errCode: Int?
)
