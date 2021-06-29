/*
 * Created by Quentin Marciset on 19/3/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.qmobile.qmobileapi.auth.AuthInfoHelper
import timber.log.Timber
import java.text.SimpleDateFormat

object UserInfoDateFormatter {

    // Store UserInfo
    @SuppressLint("SimpleDateFormat")
    fun storeUserInfo(userInfo: JsonObject, preferences: SharedPreferences) {
        userInfoIterator(userInfo)
        Timber.v("Store user info $userInfo")
        preferences[AuthInfoHelper.USER_INFO] = userInfo.toString()
    }

    private fun userInfoIterator(userInfo: JsonObject) {
        userInfo.keySet().forEach { key ->
            userInfo.get(key).toString().let { value ->
                formatDate(value.split("\"").getOrNull(1))?.let { formattedDated ->
                    userInfo.addProperty(key, formattedDated)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private val formatDate: (String?) -> String? = { dateString: String? ->
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.safeParse(dateString)?.let { date ->
            SimpleDateFormat("yy!MM!dd").format(date).toString()
        }
    }
}
