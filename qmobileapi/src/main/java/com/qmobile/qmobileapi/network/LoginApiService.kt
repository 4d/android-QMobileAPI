/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.network

import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.model.auth.LogoutResponse
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginApiService {

    /*
     * AUTHENTICATION
     */

    /**
     * Authenticates the user with given credentials. Returns a JSON with session id and token
     */
    @POST("\$authenticate")
    fun authenticate(@Body body: RequestBody): Single<Response<ResponseBody>>

    /**
     * Performs authenticate synchronously
     */
    @POST("\$authenticate")
    fun syncAuthenticate(@Body body: RequestBody?): Call<AuthResponse>

    /**
     * Logs out current user. Session token given in header will be extracted to remove the
     * session in memory and disk
     */
    @POST("\$logout")
    fun logout(): Single<Response<ResponseBody>>

    /**
     * Performs logout synchronously
     */
    @POST("\$logout")
    fun syncLogout(): Call<LogoutResponse>

    /**
     * Returns true if enough licenses, else returns an error code
     */
    @GET("licensecheck")
    fun licenseCheck(): Single<Response<ResponseBody>>
}
