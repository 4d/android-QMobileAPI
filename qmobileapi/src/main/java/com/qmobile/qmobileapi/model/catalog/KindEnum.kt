/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.catalog

import com.google.gson.annotations.SerializedName

enum class KindEnum {
    @SerializedName("storage")
    STORAGE,
    @SerializedName("calculated")
    CALCULATED,
    @SerializedName("related entity")
    RELATED_ENTITY,
    @SerializedName("alias")
    ALIAS
}
