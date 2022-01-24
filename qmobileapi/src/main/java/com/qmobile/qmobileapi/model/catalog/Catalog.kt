/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.catalog

import com.fasterxml.jackson.annotation.JsonProperty

data class Catalog(
    @JsonProperty val __UNIQID: String? = null,
    @JsonProperty val dataClasses: ArrayList<DataClass>? = null
)
