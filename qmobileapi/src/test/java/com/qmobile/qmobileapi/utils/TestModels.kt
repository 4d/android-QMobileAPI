/*
 * Created by Quentin Marciset on 23/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.qmobile.qmobileapi.model.entity.Entities
import com.qmobile.qmobileapi.model.entity.EntityModel

data class EventApiTest(
    @JsonProperty val cancellable: Boolean? = null,
    @JsonProperty val count: Int? = null,
    @JsonProperty val id: Int? = null,
    @JsonProperty val json: Any? = null,
    @JsonProperty val picture: Any? = null,
    @JsonProperty val timeStamp: String? = null,
    @JsonProperty val title: String? = null,
    @JsonProperty val location: LocationApiTest? = null,
    @JsonProperty val guests: Entities<EmployeeApiTest>? = null,
    @JsonProperty val organizer: EmployeeApiTest? = null,
    override val __GlobalStamp: Int? = null,
    override val __KEY: String,
    override val __STAMP: Int? = null,
    override val __TIMESTAMP: String? = null
) : EntityModel {
    @JsonCreator
    private constructor() : this(__KEY = "")
}

data class LocationApiTest(
    @JsonProperty val name: String? = null,
    @JsonProperty val zipCode: String? = null,
    override val __GlobalStamp: Int? = null,
    override val __KEY: String,
    override val __STAMP: Int? = null,
    override val __TIMESTAMP: String? = null
) : EntityModel

data class EmployeeApiTest(
    @JsonProperty val firstName: String? = null,
    @JsonProperty val lastName: String? = null,
    @JsonProperty val organizedEvent: EventApiTest? = null,
    @JsonProperty val participatedEvents: Entities<EventApiTest>? = null,
    override val __GlobalStamp: Int? = null,
    override val __KEY: String,
    override val __STAMP: Int? = null,
    override val __TIMESTAMP: String? = null
) : EntityModel

data class EmployeeExtendedAttributes(
    @JsonProperty val ID: Int? = null,
    @JsonProperty val Address: String? = null,
    @JsonProperty val Company: String? = null,
    @JsonProperty val Email: String? = null,
    @JsonProperty var FirstName: String? = null,
    @JsonProperty val Job: String? = null,
    @JsonProperty var LastName: String? = null,
    @JsonProperty val Location: String? = null,
    @JsonProperty val Notes: String? = null,
    @JsonProperty val Phone: String? = null,
    @JsonProperty val service: ServiceExtendedAttributes? = null,
    @JsonProperty val serviceManaged: Entities<ServiceExtendedAttributes>? = null,
    override val __KEY: String,
    override val __GlobalStamp: Int? = null,
    override val __STAMP: Int? = null,
    override val __TIMESTAMP: String? = null
) : EntityModel

data class ServiceExtendedAttributes(
    @JsonProperty val ID: Int? = null,
    @JsonProperty val name: String,
    @JsonProperty val comment: String? = null,
    @JsonProperty val employeeNumber: Int? = null,
    @JsonProperty val budget: String? = null,
    @JsonProperty val manager: EmployeeExtendedAttributes? = null,
    @JsonProperty val employees: Entities<EmployeeExtendedAttributes>? = null,
    override val __KEY: String,
    override val __STAMP: Int? = null,
    override val __GlobalStamp: Int? = null,
    override val __TIMESTAMP: String? = null
) : EntityModel {
    @JsonCreator
    private constructor() : this(name = "", __KEY = "")
}
