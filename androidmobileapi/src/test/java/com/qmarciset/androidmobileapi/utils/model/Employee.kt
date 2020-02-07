/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.utils.model

import com.qmarciset.androidmobileapi.model.entity.EntityModel

@Suppress("ConstructorParameterNaming")
class Employee(
    val ID: Int? = null,
    val Address: String? = null,
    val Company: String? = null,
    val Email: String? = null,
    val FirstName: String? = null,
    val Job: String? = null,
    val LastName: String? = null,
    val Location: String? = null,
    val Notes: String? = null,
    val Phone: String? = null,
    val Photo: com.qmarciset.androidmobileapi.utils.model.Photo? = null,
    override val __GlobalStamp: Int? = null,
    override val __KEY: String,
    override val __STAMP: Int? = null,
    override val __TIMESTAMP: String? = null,
    override val __entityModel: String? = null
) : EntityModel
