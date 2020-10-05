/*
 * Created by Quentin Marciset on 23/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.qmobile.qmobileapi.model.entity.Entities
import com.qmobile.qmobileapi.model.entity.EntityModel

data class EventApiTest(
    val cancellable: Boolean? = null,
    val count: Int? = null,
    val id: Int? = null,
    val json: Any? = null,
    val picture: Any? = null,
    val timeStamp: String? = null,
    val title: String? = null,
    val location: LocationApiTest? = null,
    val guests: Entities<EmployeeApiTest>? = null,
    val organizer: EmployeeApiTest? = null,
    override val __GlobalStamp: Int?,
    override val __KEY: String?,
    override val __STAMP: Int?,
    override val __TIMESTAMP: String?
) : EntityModel

data class LocationApiTest(
    val name: String? = null,
    val zipCode: String? = null,
    override val __GlobalStamp: Int?,
    override val __KEY: String?,
    override val __STAMP: Int?,
    override val __TIMESTAMP: String?
) : EntityModel

data class EmployeeApiTest(
    val firstName: String? = null,
    val lastName: String? = null,
    val organizedEvent: EventApiTest? = null,
    val participatedEvents: Entities<EventApiTest>? = null,
    override val __GlobalStamp: Int?,
    override val __KEY: String?,
    override val __STAMP: Int?,
    override val __TIMESTAMP: String?
) : EntityModel

data class EmployeeExtendedAttributes(
    val ID: Int? = null,
    val Address: String? = null,
    val Company: String? = null,
    val Email: String? = null,
    var FirstName: String? = null,
    val Job: String? = null,
    var LastName: String? = null,
    val Location: String? = null,
    val Notes: String? = null,
    val Phone: String? = null,
    val service: ServiceExtendedAttributes? = null,
    val serviceManaged: Entities<ServiceExtendedAttributes>? = null,
    override val __GlobalStamp: Int?,
    override val __KEY: String?,
    override val __STAMP: Int?,
    override val __TIMESTAMP: String?
) : EntityModel

data class ServiceExtendedAttributes(
    val ID: Int? = null,
    val name: String,
    val comment: String? = null,
    val employeeNumber: Int? = null,
    val budget: String? = null,
    val manager: EmployeeExtendedAttributes? = null,
    val employees: Entities<EmployeeExtendedAttributes>? = null,
    override val __KEY: String,
    override val __STAMP: Int? = null,
    override val __GlobalStamp: Int? = null,
    override val __TIMESTAMP: String? = null
) : EntityModel
