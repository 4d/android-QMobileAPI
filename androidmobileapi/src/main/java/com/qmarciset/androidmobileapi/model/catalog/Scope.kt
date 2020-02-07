/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.catalog

import com.google.gson.annotations.SerializedName

enum class Scope {
    @SerializedName("public")
    PUBLIC,
    @SerializedName("public on server")
    PUBLIC_ON_SERVER,
    @SerializedName("protected")
    PROTECTED,
    @SerializedName("private")
    PRIVATE
}
