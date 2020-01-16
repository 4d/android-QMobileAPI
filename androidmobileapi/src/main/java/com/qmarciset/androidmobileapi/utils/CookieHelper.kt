package com.qmarciset.androidmobileapi.utils

import android.content.Context
import java.io.File
import okhttp3.Headers

object CookieHelper {

    private const val COOKIE = "Cookie"
    private const val LAST_COOKIE = "Last_Cookie"

    fun writeCookie(cookieValue: String) {
        val file = File(COOKIE)
        file.writeText(cookieValue)
    }

    fun deleteCookie() {
        val file = File(COOKIE)
        file.delete()
    }

    fun getSavedCookieFromFile(): String? {
        val file = File(COOKIE)
        if (file.exists()) {
            val bufferedReader = file.bufferedReader()
            val text: List<String> = bufferedReader.readLines()
            if (text.isNotEmpty()) {
                val cookie = text[0]
                if (cookie.isNotEmpty()) {
                    return cookie
                }
            }
        }
        return null
    }

    fun saveCookieInPref(context: Context, cookie: String?) {
        val prefs = defaultPrefs(context)
        prefs[COOKIE] = cookie
    }

    fun getCookieFromPref(context: Context): String? {
        val prefs = defaultPrefs(context)
        return prefs[COOKIE]
    }

    fun saveLastOkRequestCookieInPref(context: Context, cookie: String?) {
        val prefs = defaultPrefs(context)
        prefs[LAST_COOKIE] = cookie
    }

    fun getLastOkRequestCookieFromPref(context: Context): String? {
        val prefs = defaultPrefs(context)
        return prefs[LAST_COOKIE]
    }

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
