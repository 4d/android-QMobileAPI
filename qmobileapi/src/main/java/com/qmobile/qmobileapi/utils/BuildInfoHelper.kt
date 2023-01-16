/*
 * Created by qmarciset on 16/1/2023.
 * 4D SAS
 * Copyright (c) 2023 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import org.json.JSONObject

object BuildInfoHelper {

    fun build(appInfoJsonObj: JSONObject) =
        JSONObject().apply {
            appInfoJsonObj.getSafeObject("buildInfo")?.let {
                put("componentBuild", it.getSafeString("componentBuild") ?: "")
                put("ideBuildVersion", it.getSafeString("ideBuildVersion") ?: "")
                put("ideVersion", it.getSafeString("ideVersion") ?: "")
            }
        }
}
