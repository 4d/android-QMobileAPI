/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.catalog

import com.fasterxml.jackson.annotation.JsonProperty

enum class KindEnum {
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
