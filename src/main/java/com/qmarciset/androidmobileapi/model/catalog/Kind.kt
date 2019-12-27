package com.qmarciset.androidmobileapi.model.catalog

import com.google.gson.annotations.SerializedName

enum class Kind {
    @SerializedName("storage")
    STORAGE,
    @SerializedName("calculated")
    CALCULATED,
    @SerializedName("related entity")
    RELATED_ENTITY,
    @SerializedName("alias")
    ALIAS
}
