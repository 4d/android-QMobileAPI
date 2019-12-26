package com.qmarciset.androidmobileapi.model.catalog

data class DataClass(
    val name: String?,
    val dataURI: String?,
    val uri: String?,
    val scope: Scope?,
    val attributes: List<Attribute?>?,
    val className: String?,
    val collectionName: String?,
    val key: List<Key?>?
)
