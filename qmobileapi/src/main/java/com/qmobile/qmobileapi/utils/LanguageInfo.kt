/*
 * Created by qmarciset on 13/7/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import org.json.JSONObject
import java.util.Locale

object LanguageInfo {

    private const val LANGUAGE_ID = "id"
    private const val LANGUAGE_CODE = "code"
    private const val LANGUAGE_REGION = "region"

    fun build() = JSONObject().apply {
        put(LANGUAGE_ID, Locale.getDefault().toString())
        put(LANGUAGE_CODE, Locale.getDefault().language)
        put(LANGUAGE_REGION, Locale.getDefault().country)
    }
}
