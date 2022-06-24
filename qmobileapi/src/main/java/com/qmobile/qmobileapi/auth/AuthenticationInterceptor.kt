/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.qmobile.qmobileapi.model.error.ErrorResponse
import com.qmobile.qmobileapi.network.HeaderHelper.addAuthorizationHeader
import com.qmobile.qmobileapi.network.HeaderHelper.addContentTypeHeader
import com.qmobile.qmobileapi.network.HeaderHelper.addCookies
import com.qmobile.qmobileapi.network.HeaderHelper.addXQMobileHeader
import com.qmobile.qmobileapi.network.HeaderHelper.clearAuthorizationHeader
import com.qmobile.qmobileapi.network.LoginApiService
import com.qmobile.qmobileapi.repository.AuthRepository
import com.qmobile.qmobileapi.utils.LoginRequiredCallback
import com.qmobile.qmobileapi.utils.RequestErrorHelper.tryToParseError
import com.qmobile.qmobileapi.utils.RestErrorCode
import com.qmobile.qmobileapi.utils.SharedPreferencesHolder
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import timber.log.Timber
import java.net.HttpURLConnection
import java.util.concurrent.atomic.AtomicBoolean

class AuthenticationInterceptor(
    mSharedPreferencesHolder: SharedPreferencesHolder,
    mLoginApiService: LoginApiService?,
    mLoginRequiredCallback: LoginRequiredCallback?,
    private val mapper: ObjectMapper
) : Interceptor {

    private var loginApiService: LoginApiService? = null
    private var sharedPreferencesHolder: SharedPreferencesHolder
    private var loginRequiredCallback: LoginRequiredCallback? = null

    private var hasAuthBeenRefreshed = AtomicBoolean(false)
    private var isAuthInProgress = AtomicBoolean(false)

    init {
        this.loginApiService = mLoginApiService
        this.sharedPreferencesHolder = mSharedPreferencesHolder
        this.loginRequiredCallback = mLoginRequiredCallback
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Adding Content-Type and X-QMobile headers
        val requestBuilder = originalRequest.newBuilder().addContentTypeHeader().addXQMobileHeader()
            .addCookies(sharedPreferencesHolder.cookies)

        // If a token is stored in sharedPreferences, we add it in header
        if (sharedPreferencesHolder.sessionToken.isNotEmpty()) {
            Timber.d("[SessionToken retrieved in SharedPreferences : ${sharedPreferencesHolder.sessionToken}]")
            requestBuilder.clearAuthorizationHeader()
                .addAuthorizationHeader(sharedPreferencesHolder.sessionToken)
        } else {
            Timber.d("[No sessionToken retrieved in SharedPreferences]")
        }

        val request = requestBuilder.build()
        // Timber.d("[ Request = $request ]")

        var response = chain.proceed(request)

        val parsedError: ErrorResponse? = response.tryToParseError(mapper)
        parsedError?.__ERRORS?.let { errors ->
            if (errors.any { errorReason ->
                errorReason.errCode == RestErrorCode.query_placeholder_is_missing_or_null
            }
            ) {
                refreshAuth(response, chain, requestBuilder)?.let { res ->
                    response = res
                }
            }
        }

        when (response.code) {
            HttpURLConnection.HTTP_OK -> {
                // Everything is fine
            }
            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                if (hasAuthBeenRefreshed.get() && loginApiService != null) {
                    response = chain.proceed(request)
                } else {
                    refreshAuth(response, chain, requestBuilder)?.let {
                        response = it
                    }
                }
            }
            else -> {
            }
        }
        return response
    }

    private fun refreshAuth(
        response: Response,
        chain: Interceptor.Chain,
        requestBuilder: Request.Builder
    ): Response? {
        if (!isAuthInProgress.getAndSet(true) && !hasAuthBeenRefreshed.get()) {
            if (sharedPreferencesHolder.guestLogin) {
                loginApiService?.let { loginApiService ->
                    // Closing the current active response before building a new one
                    response.closeQuietly()
                    requestBuilder.refreshAuthentication(loginApiService)
                    return chain.proceed(requestBuilder.build())
                }
            } else {
                // We ask to go back to the login page as this is not a guest authenticated session
                sharedPreferencesHolder.sessionToken = ""
                loginRequiredCallback?.invoke()
            }
        }
        return null
    }

    /**
     * Refresh authentication by performing a synchronous login
     */
    private fun Request.Builder.refreshAuthentication(loginApiService: LoginApiService) {
        val authRepository = AuthRepository(loginApiService)
        val authRequestBody = sharedPreferencesHolder.buildAuthRequestBody("", "")
        val authResponse = authRepository.syncAuthenticate(authRequestBody)
        if (authResponse != null) {
            if (sharedPreferencesHolder.handleLoginInfo(authResponse)) {
                this.clearAuthorizationHeader()
                    .addAuthorizationHeader(sharedPreferencesHolder.sessionToken)
                hasAuthBeenRefreshed.set(true)
            } // else : No sessionToken could be retrieved
        } // else : Login request failed
        isAuthInProgress.set(false)
    }

    /**
     * Reinitialize AtomicBoolean to be able to perform other automatic authentication if another 401
     * occurs in another datasync
     */
    fun reinitializeInterceptorRetryState() {
        hasAuthBeenRefreshed.set(false)
    }
}
