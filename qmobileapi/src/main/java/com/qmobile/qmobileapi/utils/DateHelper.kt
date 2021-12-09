/*
 * Created by qmarciset on 9/12/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

fun SimpleDateFormat.safeParse(dateString: String?): Date? {
    if (dateString.isNullOrEmpty()) return null
    return try {
        this.parse(dateString)
    } catch (e: ParseException) {
        null
    }
}
