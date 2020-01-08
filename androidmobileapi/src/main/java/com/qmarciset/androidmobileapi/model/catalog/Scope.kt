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
