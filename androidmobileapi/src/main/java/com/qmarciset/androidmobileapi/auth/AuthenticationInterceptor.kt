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

    // For test purpose to trigger 401 error
    private var corruptedTokenCounter = 0
    private val corruptedTokenSuffix = "XXX_CORRUPTED_XXX"

    companion object {
        private const val CORRUPTED_TOKEN_TRIGGER_LIMIT = 0
    }

    private var loginApiService: LoginApiService? = null
    private var authInfoHelper: AuthInfoHelper
    private var loginRequiredCallback: LoginRequiredCallback? = null

    init {
        this.loginApiService = mLoginApiService
        this.authInfoHelper = mAuthInfoHelper
        this.loginRequiredCallback = mLoginRequiredCallback
    }

    @Suppress("LongMethod", "NestedBlockDepth")
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
            Timber.d("SessionToken retrieved in SharedPreferences : ${authInfoHelper.sessionToken}")
            requestBuilder
                .removeHeader(ApiClient.AUTHORIZATION_HEADER_KEY)
                .addHeader(
                    ApiClient.AUTHORIZATION_HEADER_KEY,
                    "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} ${authInfoHelper.sessionToken}"
                )

            // For test purpose to trigger 401 error, triggered on 3rd ApiService request
            corruptedTokenCounter++
            if (corruptedTokenCounter == CORRUPTED_TOKEN_TRIGGER_LIMIT) {
                Timber.e("[[ XXX Adding corrupted token XXX ]]")
                requestBuilder
                    .removeHeader(ApiClient.AUTHORIZATION_HEADER_KEY)
                    .addHeader(
                        ApiClient.AUTHORIZATION_HEADER_KEY,
                        "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} ${authInfoHelper.sessionToken}_$corruptedTokenSuffix"
                    )
            }
        } else {
            Timber.d("No sessionToken retrieved in SharedPreferences")
        }

        val request = requestBuilder.build()
        Timber.d("[ Request = $request ]")

        var response = chain.proceed(request)

        // Boolean to make sure we don't perform the refreshAuth() procedure twice
        var isAuthAlreadyRefreshed = false

        val parsedError: ErrorResponse? = RequestErrorHelper.tryToParseError(response)
        parsedError?.let {
            parsedError.__ERRORS?.let { errors ->
                if (errors.any { errorReason ->
                        errorReason.errCode == RestErrorCode.query_placeholder_is_missing_or_null
                    }) {
                    refreshAuth(response, chain, requestBuilder)?.let { res ->
                        response = res
                    }
                    isAuthAlreadyRefreshed = true
                }
            }
        }
        if (!isAuthAlreadyRefreshed) {
            when (response.code) {
                HttpURLConnection.HTTP_OK -> {
                    // Everything is fine
                }
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    refreshAuth(response, chain, requestBuilder)?.let {
                        response = it
                    }
                }
                else -> {
                }
            }
        }
        return response
    }

    private fun refreshAuth(
        response: Response,
        chain: Interceptor.Chain,
        requestBuilder: Request.Builder
    ): Response? {
        if (authInfoHelper.guestLogin) {
            loginApiService?.let { loginApiService ->
                // Closing the current active response before building a new one
                response.closeQuietly()
                requestBuilder.refreshAuthentication(loginApiService)
                return chain.proceed(requestBuilder.build())
            }
        } else {
            // We ask to go back to the login page as this is not a guest authenticated session
            loginRequiredCallback?.loginRequired()
        }
        return null
    }

    /**
     * Refresh authentication by performing a synchronous logout followed by a synchronous login
     */
    private fun Request.Builder.refreshAuthentication(loginApiService: LoginApiService) {
        val authRepository = AuthRepository(loginApiService)
        if (authRepository.syncLogout()) {
            val authRequest = authInfoHelper.buildAuthRequestBody("", "")
            val authResponse = authRepository.syncAuthenticate(authRequest)
            if (authResponse != null) {
                if (authInfoHelper.handleLoginInfo(authResponse)) {
                    this.removeHeader(ApiClient.AUTHORIZATION_HEADER_KEY)
                        .addHeader(
                            ApiClient.AUTHORIZATION_HEADER_KEY,
                            "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} ${authInfoHelper.sessionToken}"
                        )
                } else {
                    // No sessionToken could be retrieved
                }
            } else {
                // Login request failed
            }
        } else {
            // Logout request failed
        }
    }
}
