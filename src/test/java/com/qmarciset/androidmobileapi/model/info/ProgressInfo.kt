package com.qmarciset.androidmobileapi.model.info

@Suppress("ConstructorParameterNaming")
data class ProgressInfo(
    val UserInfo: String? = null,
    val percent: Int,
    val sessions: Int? = null
)
