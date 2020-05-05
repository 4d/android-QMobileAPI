/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.auth

import com.qmarciset.androidmobileapi.model.error.ErrorResponse
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.LoginApiService
import com.qmarciset.androidmobileapi.repository.AuthRepository
import com.qmarciset.androidmobileapi.utils.RequestErrorHelper
import com.qmarciset.androidmobileapi.utils.RestErrorCode
import java.net.HttpURLConnection
import java.util.concurrent.atomic.AtomicBoolean
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import timber.log.Timber

class AuthenticationInterceptor(
    mAuthInfoHelper: AuthInfoHelper,
    mLoginApiService: LoginApiService?,
    mLoginRequiredCallback: LoginRequiredCallback?
) : Interceptor {

    private var loginApiService: LoginApiService? = null
    private var authInfoHelper: AuthInfoHelper
    private var loginRequiredCallback: LoginRequiredCallback? = null

    private var hasAuthBeenRefreshed = AtomicBoolean(false)
    private var isAuthInProgress = AtomicBoolean(false)

    init {
        this.loginApiService = mLoginApiService
        this.authInfoHelper = mAuthInfoHelper
        this.loginRequiredCallback = mLoginRequiredCallback
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Adding Content-Type and X-QMobile headers
        val requestBuilder = originalRequest.newBuilder()
            .addHeader(
                ApiClient.CONTENT_TYPE_HEADER_KEY,
                ApiClient.CONTENT_TYPE_HEADER_VALUE
            )
            .addHeader(
                ApiClient.X_QMOBILE_HEADER_KEY,
                ApiClient.X_QMOBILE_HEADER_VALUE
            )

        // If a token is stored in sharedPreferences, we add it in header
        if (authInfoHelper.sessionToken.isNotEmpty()) {
            Timber.d("[SessionToken retrieved in SharedPreferences : ${authInfoHelper.sessionToken}]")
            requestBuilder
                .removeHeader(ApiClient.AUTHORIZATION_HEADER_KEY)
                .addHeader(
                    ApiClient.AUTHORIZATION_HEADER_KEY,
                    "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} ${authInfoHelper.sessionToken}"
                )
        } else {
            Timber.d("[No sessionToken retrieved in SharedPreferences]")
        }

        val request = requestBuilder.build()
        Timber.d("[ Request = $request ]")

        var response = chain.proceed(request)

        val parsedError: ErrorResponse? = RequestErrorHelper.tryToParseError(response)
        parsedError?.__ERRORS?.let { errors ->
            if (errors.any { errorReason ->
                    errorReason.errCode == RestErrorCode.query_placeholder_is_missing_or_null
                }) {
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
            if (authInfoHelper.guestLogin) {
                loginApiService?.let { loginApiService ->
                    // Closing the current active response before building a new one
                    response.closeQuietly()
                    requestBuilder.refreshAuthentication(loginApiService)
                    return chain.proceed(requestBuilder.build())
                }
            } else {
                // We ask to go back to the login page as this is not a guest authenticated session
                authInfoHelper.sessionToken = ""
                loginRequiredCallback?.loginRequired()
            }
        }
        return null
    }

    /**
     * Refresh authentication by performing a synchronous login
     */
    private fun Request.Builder.refreshAuthentication(loginApiService: LoginApiService) {
        val authRepository = AuthRepository(loginApiService)
        val authRequest = authInfoHelper.buildAuthRequestBody("", "")
        val authResponse = authRepository.syncAuthenticate(authRequest)
        if (authResponse != null) {
            if (authInfoHelper.handleLoginInfo(authResponse)) {
                this.removeHeader(ApiClient.AUTHORIZATION_HEADER_KEY)
                    .addHeader(
                        ApiClient.AUTHORIZATION_HEADER_KEY,
                        "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} ${authInfoHelper.sessionToken}"
                    )
                hasAuthBeenRefreshed.set(true)
            } else {
                // No sessionToken could be retrieved
            }
        } else {
            // Login request failed
        }
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
