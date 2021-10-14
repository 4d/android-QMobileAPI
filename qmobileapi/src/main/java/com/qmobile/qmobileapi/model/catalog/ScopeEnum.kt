/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.catalog

import com.fasterxml.jackson.annotation.JsonProperty

enum class ScopeEnum {
    @JsonProperty("public")
    PUBLIC,

    @JsonProperty("public on server")
    PUBLIC_ON_SERVER,

    @JsonProperty("protected")
    PROTECTED,

    @JsonProperty("private")
    PRIVATE
}
