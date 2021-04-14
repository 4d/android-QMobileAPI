/*
 * Created by Quentin Marciset on 14/4/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

fun String.extractJSON(): String? {
    val start = this.indexOf("{")
    val end = this.lastIndexOf("}")
    if (start >= 0 && end >= 0) {
        return this.substring(start, end + 1)
    }
    return null
}
