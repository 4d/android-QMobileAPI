package com.qmarciset.androidmobileapi.utils

import android.content.Context
import org.json.JSONObject

class AuthInfoHelper(context: Context) {

    private val prefs = defaultPrefs(context)

    // Application Info
    var appInfo: JSONObject
        get() = JSONObject(prefs[AUTH_APPLICATION] ?: "")
        set(value) {
            prefs[AUTH_APPLICATION] = value
        }

    // Device Info
    var device: JSONObject
        get() = JSONObject(prefs[AUTH_DEVICE] ?: "")
        set(value) {
            prefs[AUTH_DEVICE] = value
        }

    // Team Info
    var team: JSONObject
        get() = JSONObject(prefs[AUTH_TEAM] ?: "")
        set(value) {
            prefs[AUTH_TEAM] = value
        }

    // Language Info
    var language: JSONObject
        get() = JSONObject(prefs[AUTH_LANGUAGE] ?: "")
        set(value) {
            prefs[AUTH_LANGUAGE] = value
        }

    var guestLogin: Boolean
        get() = prefs[GUEST_LOGIN] ?: false
        set(value) {
            prefs[GUEST_LOGIN] = value
        }

    fun clearAuthInfo() {
        prefs[AUTH_APPLICATION] = null
        prefs[AUTH_DEVICE] = null
        prefs[AUTH_TEAM] = null
        prefs[AUTH_LANGUAGE] = null
    }
}
