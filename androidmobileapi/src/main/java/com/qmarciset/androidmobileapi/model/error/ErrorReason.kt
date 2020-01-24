package com.qmarciset.androidmobileapi.model.error

data class ErrorReason(
    val message: String?,
    val componentSignature: String?,
    val errCode: Int?
)
