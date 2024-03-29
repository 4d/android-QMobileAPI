/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

// Constant for the number of retries when $authenticate method fails
const val MAX_LOGIN_RETRY = 1

// Constant timeout value for server pinging (in ms)
const val PING_TIMEOUT = 5000

// Table name for deleted records
const val DELETED_RECORDS = "__DeletedRecords"

// POST request body information
const val APP_JSON = "application/json"
const val APP_OCTET = "application/octet"
const val UTF8_CHARSET = "charset=utf-8"
const val UTF8 = "utf-8"

// 4D server globalStamp property name
const val GLOBALSTAMP_PROPERTY = "__GlobalStamp"

// Queries constants
object Query {
    const val QUERY_PROPERTY = "__Query"
    const val QUERY_STRING_PROPERTY = "queryString"
    const val SETTINGS = "settings"
    const val PARAMETERS = "parameters"
}
