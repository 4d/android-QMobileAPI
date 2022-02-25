/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.catalog

import com.fasterxml.jackson.annotation.JsonProperty

data class Attribute(
    val identifying: Boolean?,
    val indexed: Boolean?,
    val kind: Kind?,
    val name: String?,
    val scope: Scope?,
    val simpleDate: Boolean?,
    val type: String?
) {
    enum class Kind {
        @JsonProperty("storage")
        STORAGE,

        @JsonProperty("calculated")
        CALCULATED,

        @JsonProperty("relatedEntity")
        RELATED_ENTITY,

        @JsonProperty("relatedEntities")
        RELATED_ENTITIES,

        @JsonProperty("alias")
        ALIAS
    }

    enum class Scope {
        @JsonProperty("public")
        PUBLIC,

        @JsonProperty("public on server")
        PUBLIC_ON_SERVER,

        @JsonProperty("protected")
        PROTECTED,

        @JsonProperty("private")
        PRIVATE
    }
}
