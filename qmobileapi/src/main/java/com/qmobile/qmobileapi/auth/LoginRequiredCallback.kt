/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.auth

/**
 * Interface to ask the activity to go to login page
 */
interface LoginRequiredCallback {

    fun loginRequired()
}
