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
        val listOfKeys = userInfo.keySet()
        for (index in 0 until listOfKeys.size) {
            val key = listOfKeys.elementAt(index)
            try {
                userInfo.addProperty(
                    key,
                    formatDate(userInfo.get(key).toString().split("\"")[1])
                )
            } catch (e: Exception) { // no-opt
                Timber.d("Exception in userInfoIterator, error: ${e.localizedMessage}")
            }
        }
    }

    private val formatDate = { valueToParse: String ->
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(valueToParse)
        SimpleDateFormat("yy!MM!dd").format(date!!)
    }
}
