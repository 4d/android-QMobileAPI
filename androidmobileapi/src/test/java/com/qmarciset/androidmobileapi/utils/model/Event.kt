/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.utils.model

import com.qmarciset.androidmobileapi.model.entity.Entities
import com.qmarciset.androidmobileapi.model.entity.EntityModel

data class Event(
    val cancellable: Boolean? = null,
    val count: Int? = null,
    val id: Int? = null,
    val json: Any? = null,
    val picture: Any? = null,
    val timeStamp: String? = null,
    val title: String? = null,
    val location: Location? = null,
    val guests: Entities? = null,
    override val __GlobalStamp: Int?,
    override val __KEY: String?,
    override val __STAMP: Int?,
    override val __TIMESTAMP: String?,
    override val __entityModel: String?
) : EntityModel

data class Location(
    val name: String? = null,
    val zipCode: String? = null,
    override val __GlobalStamp: Int?,
    override val __KEY: String?,
    override val __STAMP: Int?,
    override val __TIMESTAMP: String?,
    override val __entityModel: String?
) : EntityModel

data class Employee(
    val firstName: String? = null,
    val lastName: String? = null,
    override val __GlobalStamp: Int?,
    override val __KEY: String?,
    override val __STAMP: Int?,
    override val __TIMESTAMP: String?,
    override val __entityModel: String?
) : EntityModel
