package com.qmobile.qmobileapi.network

import okhttp3.Request

object HeaderHelper {

    const val AUTHORIZATION_HEADER_KEY = "Authorization"
    const val AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer"
    const val CONTENT_TYPE_HEADER_KEY = "Content-Type"
    const val CONTENT_TYPE_HEADER_VALUE = "application/json"
    const val X_QMOBILE_HEADER_KEY = "X-QMobile"
    const val X_QMOBILE_HEADER_VALUE = "1"
    const val COOKIE_HEADER_KEY = "Set-Cookie"

    fun Request.Builder.addContentTypeHeader() = this.apply {
        addHeader(CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VALUE)
    }

    fun Request.Builder.addXQMobileHeader() = this.apply {
        addHeader(X_QMOBILE_HEADER_KEY, X_QMOBILE_HEADER_VALUE)
    }

    fun Request.Builder.addAuthorizationHeader(token: String) = this.apply {
        addHeader(AUTHORIZATION_HEADER_KEY, "$AUTHORIZATION_HEADER_VALUE_PREFIX $token")
    }

    fun Request.Builder.clearAuthorizationHeader() = this.apply {
        removeHeader(AUTHORIZATION_HEADER_KEY)
    }

    fun Request.Builder.addCookies(cookies: String) = this.apply {
        if (cookies.isNotEmpty())
            addHeader(COOKIE_HEADER_KEY, cookies)
    }
}
