/*
 * Created by htemanni on 13/9/2021.
 * 4D SAS
 * Copyright (c) 2021 htemanni. All rights reserved.
 */

package com.qmobile.qmobileapi.model.action

import java.io.Serializable

data class ActionResponse(
    val success: Boolean,
    val statusText: String?,
    val dataSynchro: Boolean?,
    val errors: List<ActionError>
)

data class ActionError(val parameter: String, val message: String) : Serializable {
    companion object {
        const val serialVersionUID = 1L
    }
}
