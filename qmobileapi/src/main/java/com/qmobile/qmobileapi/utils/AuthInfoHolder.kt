/*
 * Created by qmarciset on 13/7/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import org.json.JSONObject

object AuthInfoHolder {

    fun buildAppInfo(appInfoJsonObj: JSONObject) =
        JSONObject().apply {
            appInfoJsonObj.getSafeObject("appData")?.let {
                put("id", it.getSafeString("id") ?: "")
                put("name", it.getSafeString("name") ?: "")
                put("version", it.getSafeString("version") ?: "")
            }
        }

    fun buildTeam(appInfoJsonObj: JSONObject) = JSONObject().apply {
        this.put("id", appInfoJsonObj.getSafeString("teamId") ?: "")
    }
}
