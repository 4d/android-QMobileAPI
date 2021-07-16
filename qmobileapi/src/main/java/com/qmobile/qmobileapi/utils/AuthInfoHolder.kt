/*
 * Created by qmarciset on 13/7/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.content.Context
import org.json.JSONObject

object AuthInfoHolder {

    fun buildAppInfo(applicationId: String, versionName: String, versionCode: Int) =
        JSONObject().apply {
            put(
                "id",
                applicationId
            ) // com.qmobile.sample4dapp
            put("name", versionName) // 1.0
            put("version", versionCode) // 1
        }

    fun buildTeam(context: Context) = JSONObject().apply {
        val appInfoJsonObj = JSONObject(readContentFromFile(context, "app_info.json"))
        val newTeam = appInfoJsonObj.getSafeObject("team")
        this.put("id", newTeam?.getSafeString("TeamName") ?: "")
        this.put("name", newTeam?.getSafeString("TeamID") ?: "")
    }
}
