/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.utils

// Obsolete
const val BASE_URL = "http://192.168.5.10:8099/mobileapp/"

// Set to `true` if cookies WASID4D and WAKTRANSID should be used with headers
const val COOKIES_ARE_HANDLED = false

// Constant for the number of retries when $authenticate method fails
const val MAX_LOGIN_RETRY = 1

// Constant timeout value for server pinging (in ms)
const val PING_TIMEOUT = 5000

// Table name for deleted records
const val DELETED_RECORDS = "__DeletedRecords"
