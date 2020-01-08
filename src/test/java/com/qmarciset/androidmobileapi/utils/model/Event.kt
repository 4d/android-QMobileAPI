package com.qmarciset.androidmobileapi.utils.model

import com.qmarciset.androidmobileapi.model.entity.EntityModel

data class Event(
    val cancellable: Boolean? = null,
    val count: Int? = null,
    val id: Int? = null,
    val json: Any? = null,
    val picture: Any? = null,
    val timeStamp: String? = null,
    val title: String? = null,
    override val __GlobalStamp: Int?,
    override val __KEY: String?,
    override val __STAMP: Int?,
    override val __TIMESTAMP: String?,
    override val __entityModel: String?
) : EntityModel
