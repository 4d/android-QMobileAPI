package com.qmarciset.androidmobileapi.utils

import okhttp3.Headers

object CookieHelper {

    // Utility function to concatenate cookies
    fun buildCookieString(headers: Headers): String? {
        val headersMap = headers.toMultimap()
        val cookies = headersMap["Set-Cookie"]
        var cookieString = ""
        var nbCookie = 0
        cookies?.forEach {
            val splitCookie = it.split("=", ";")
            val cookieKey = splitCookie[0]
            val cookieValue = splitCookie[1]
            cookieString = "$cookieString$cookieKey=$cookieValue; "
            nbCookie++
        }
        // We must have 2 cookies concatenated
        if (nbCookie > 1)
            return cookieString
        return null
    }
}
