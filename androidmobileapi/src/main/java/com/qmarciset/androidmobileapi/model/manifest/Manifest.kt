/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.manifest

import org.json.JSONObject

data class Manifest(
    val team: JSONObject?,
    val guestLogin: Boolean,
    val remoteUrl: String?
)
