package com.qmarciset.androidmobileapi.auth

import android.content.Context
import com.qmarciset.androidmobileapi.utils.SingletonHolder
import com.qmarciset.androidmobileapi.utils.customPrefs
import com.qmarciset.androidmobileapi.utils.defaultPrefs
import com.qmarciset.androidmobileapi.utils.get
import com.qmarciset.androidmobileapi.utils.set
import java.util.UUID
import org.json.JSONObject

class AuthInfoHelper(context: Context) {

    companion object : SingletonHolder<AuthInfoHelper, Context>(::AuthInfoHelper) {
        const val AUTH_EMAIL = "email"
        const val AUTH_PASSWORD = "password"
        const val AUTH_APPLICATION = "application"
        const val AUTH_DEVICE = "device"
        const val AUTH_TEAM = "team"
        const val AUTH_LANGUAGE = "language"
        const val AUTH_PARAMETERS = "parameters"

        const val GUEST_LOGIN = "guest_login"
        const val SESSION_ID = "session_id"
        const val SESSION_TOKEN = "session_token"
        const val DEVICE_UUID = "device_uuid"

        const val PRIVATE_PREF_NAME = "4D_QMOBILE_PRIVATE"
    }

    private val prefs =
        defaultPrefs(context)

    private val privatePrefs =
        customPrefs(context, PRIVATE_PREF_NAME)

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

    val deviceUUID: String
    get() = privatePrefs[DEVICE_UUID] ?: kotlin.run {
        val uuid = UUID.randomUUID().toString()
        privatePrefs[DEVICE_UUID] = uuid
        return@run uuid
    }

    var sessionId: String
        get() = privatePrefs[SESSION_ID] ?: ""
        set(value) {
            privatePrefs[SESSION_ID] = value
        }

    var sessionToken: String
        get() = privatePrefs[SESSION_TOKEN] ?: ""
        set(value) {
            privatePrefs[SESSION_TOKEN] = value
        }
}
