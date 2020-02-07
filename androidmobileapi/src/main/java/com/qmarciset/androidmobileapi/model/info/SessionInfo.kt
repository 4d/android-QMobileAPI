/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.info

data class SessionInfo(
    val expiration: String? = null,
    val lifeTime: Int? = null,
    val sessionID: String? = null,
    val userID: String? = null,
    val userName: String? = null
)
