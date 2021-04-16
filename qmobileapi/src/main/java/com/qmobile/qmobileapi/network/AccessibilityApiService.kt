/*
 * Created by Quentin Marciset on 12/4/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.network

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface AccessibilityApiService {

    /*
     * SERVER ACCESSIBILITY
     */

    @GET("/mobileapp/")
    fun checkAccessibility(): Single<Response<ResponseBody>>
}
