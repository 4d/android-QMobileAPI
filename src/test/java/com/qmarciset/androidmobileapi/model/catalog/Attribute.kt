package com.qmarciset.androidmobileapi.model.catalog

data class Attribute(
    val identifying: Boolean?,
    val indexed: Boolean?,
    val kind: Kind?,
    val name: String?,
    val scope: Scope?,
    val simpleDate: Boolean?,
    val type: String?
)
