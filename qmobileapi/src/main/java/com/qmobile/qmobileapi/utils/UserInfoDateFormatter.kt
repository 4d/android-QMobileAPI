/*
 * Created by Quentin Marciset on 19/3/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.fasterxml.jackson.databind.ObjectMapper
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

object UserInfoDateFormatter {

    // Store UserInfo
    fun storeUserInfo(userInfo: Map<String, Any>, sharedPreferencesHolder: SharedPreferencesHolder) {
        val formattedUserInfo = formatDateValues(userInfo)
        Timber.v("Store user info $formattedUserInfo")
        sharedPreferencesHolder.userInfo = ObjectMapper().parseToString(formattedUserInfo)
    }

    private fun formatDateValues(userInfo: Map<String, Any>): Map<String, Any> {
        val mutableUserInfo = userInfo.toMutableMap()
        for ((key, value) in mutableUserInfo) {
            formatDate(value.toString().split("\"").getOrNull(1))?.let { formattedDated ->
                mutableUserInfo[key] = formattedDated
            }
        }
        return mutableUserInfo
    }

    private val formatDate: (String?) -> String? = { dateString: String? ->
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.safeParse(dateString)?.let { date ->
            SimpleDateFormat("yy!MM!dd", Locale.getDefault()).format(date).toString()
        }
    }
}
