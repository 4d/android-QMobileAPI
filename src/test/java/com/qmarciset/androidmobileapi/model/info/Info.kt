package com.qmarciset.androidmobileapi.model.info

@Suppress("ConstructorParameterNaming")
data class Info(
    val entitySetCount: Int = 0,
    val ProgressInfo: List<ProgressInfo>? = null,
    val cacheSize: Int? = null,
    val entitySet: List<EntitySet>,
    val jsContextInfo: List<JsContextInfo>? = null,
    val sessionInfo: List<SessionInfo>? = null,
    val usedCache: Int? = null
)
