package com.qmarciset.androidmobileapi.model.info

data class JsContextInfo(
    val activeDebugger: Boolean? = null,
    val contextPoolSize: Int? = null,
    val createdContextCount: Int? = null,
    val destroyedContextCount: Int? = null,
    val reusableContextCount: Int? = null,
    val unusedContextCount: Int? = null,
    val usedContextCount: Int? = null,
    val usedContextMaxCount: Int? = null
)
