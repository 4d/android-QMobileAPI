/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.repository

import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.utils.APP_JSON
import com.qmobile.qmobileapi.utils.MAX_LOGIN_RETRY
import com.qmobile.qmobileapi.utils.UTF8_CHARSET
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

open class AuthRepository(private val loginApiService: LoginApiService) {

    var disposable: CompositeDisposable = CompositeDisposable()

    /**
     * Performs synchronous $authenticate request
     */
    fun syncAuthenticate(jsonRequestBody: JSONObject): AuthResponse? {
        val body = jsonRequestBody.toString()
            .toRequestBody("$APP_JSON; $UTF8_CHARSET".toMediaTypeOrNull())

        val authResponse = loginApiService.syncAuthenticate(body).execute().body()

        authResponse?.let {
            if (authResponse.success) {
                return authResponse
            }
        }
        return null
    }

    /**
     * Performs asynchronous $authenticate request
     */
    fun authenticate(
        jsonRequestBody: JSONObject,
        shouldRetryOnError: Boolean,
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        val body = jsonRequestBody.toString()
            .toRequestBody("$APP_JSON; $UTF8_CHARSET".toMediaTypeOrNull())

        disposable.add(
            loginApiService.authenticate(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry { integer, _ ->
                    // Retries authentication MAX_LOGIN_RETRY times
                    Timber.d("Retrying \$authenticate automatically")
                    integer <= MAX_LOGIN_RETRY && shouldRetryOnError
                }
                .subscribeWith(DisposableSingleObserver(onResult))
        )
    }

    /**
     * Performs synchronous $logout request
     */
    fun syncLogout(): Boolean {
        val logoutResponse = loginApiService.syncLogout().execute().body()
        return logoutResponse?.ok ?: false
    }

    /**
     * Performs asynchronous $logout request
     */
    fun logout(
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            loginApiService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DisposableSingleObserver(onResult))
        )
    }

    /**
     * Performs $licensecheck request
     */
    fun licenseCheck(onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit) {
        disposable.add(
            loginApiService.licenseCheck()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DisposableSingleObserver(onResult))
        )
    }
}
