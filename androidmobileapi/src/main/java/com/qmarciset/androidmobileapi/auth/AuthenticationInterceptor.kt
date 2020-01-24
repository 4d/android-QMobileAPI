package com.qmarciset.androidmobileapi.auth

import com.google.gson.Gson
import com.qmarciset.androidmobileapi.model.error.ErrorResponse
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.LoginApiService
import com.qmarciset.androidmobileapi.repository.AuthRepository
import com.qmarciset.androidmobileapi.utils.RestErrorCode
import com.qmarciset.androidmobileapi.utils.parseJsonToType
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

    companion object {
        private const val CORRUPTED_TOKEN_TRIGGER_LIMIT = 3
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

        val requestBuilder = originalRequest.newBuilder()
            .addHeader(
                ApiClient.CONTENT_TYPE_HEADER_KEY,
                ApiClient.CONTENT_TYPE_HEADER_VALUE
            )
            .addHeader(
                ApiClient.X_QMOBILE_HEADER_KEY,
                ApiClient.X_QMOBILE_HEADER_VALUE
            )

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
                        "${ApiClient.AUTHORIZATION_HEADER_VALUE_PREFIX} ${authInfoHelper.sessionToken}88"
                    )
            }
        } else {
            Timber.d("No sessionToken retrieved in SharedPreferences")
        }

        val request = requestBuilder.build()
        Timber.d("[ Request = $request ]")

        var response = chain.proceed(request)

        var isAuthAlreadyRefreshed = false

        val parsedError: ErrorResponse? = tryToParseError(response)
        parsedError?.let {
            parsedError.__ERRORS?.let { errors ->
                if (errors.any { errorReason ->
                        errorReason.errCode == RestErrorCode.query_placeholder_is_missing_or_null
                    }) {
                    refreshAuth(
                        response,
                        chain,
                        requestBuilder
                    )?.let { res ->
                        response = res
                    }
                    isAuthAlreadyRefreshed = true
                }
            }
        }
        if (!isAuthAlreadyRefreshed) {
            when (response.code) {
                HttpURLConnection.HTTP_OK -> {
                }
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    refreshAuth(
                        response,
                        chain,
                        requestBuilder
                    )?.let { response = it }
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
                response.closeQuietly()
                requestBuilder.refreshAuthentication(loginApiService)
                return chain.proceed(requestBuilder.build())
            }
        } else {
            loginRequiredCallback?.loginRequired()
        }
        return null
    }

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
                }
            }
        }
    }
}

fun tryToParseError(response: Response): ErrorResponse? {
    // If buffer is read here, it won't be readable later to decode the response.
    // Therefore, we use peekBody() to copy the buffer instead of body()
    val copyResponse = response.peekBody(Long.MAX_VALUE)
//    val responseBody = response.body
    val json = copyResponse.string()
    return Gson().parseJsonToType<ErrorResponse>(json)
}
