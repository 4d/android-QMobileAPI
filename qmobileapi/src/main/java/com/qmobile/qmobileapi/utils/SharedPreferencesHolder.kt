/*
 * Created by qmarciset on 13/7/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.app.NotificationManager
import android.content.Context
import android.webkit.CookieManager
import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.network.HeaderHelper
import com.qmobile.qmobileapi.utils.FileHelper.readContentFromFile
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

/**
 * Helper class to store authentication information into SharedPreferences
 */
open class SharedPreferencesHolder(val context: Context) {

    companion object :
        SingletonHolder<SharedPreferencesHolder, Context>(::SharedPreferencesHolder) {
        const val AUTH_EMAIL = "email"
        const val AUTH_PASSWORD = "password"
        const val AUTH_APPLICATION = "application"
        const val AUTH_DEVICE = "device"
        const val AUTH_TEAM = "team"
        const val AUTH_LANGUAGE = "language"
        const val AUTH_PARAMETERS = "parameters"

        const val GUEST_LOGIN = "guest_login"
        const val REMOTE_URL = "remote_url"
        const val SESSION_ID = "session_id"
        const val SESSION_TOKEN = "session_token"

        const val GLOBAL_STAMP = "__GlobalStamp"
        const val DELETED_RECORDS_STAMP = "__Stamp"

        const val COOKIES = "Cookie"

        const val PRIVATE_PREF_NAME = "4D_QMOBILE_PRIVATE"

        const val USER_INFO = "userInfo"

        const val PARAMETERS_TO_SORT_WITH = "parameters_to_sort_with"

        const val LAST_LOGIN_MAIL = "last_login_mail"

        const val CRASH_LOG_SAVED_FOR_LATER = "crash_log_saved_for_later"
        const val BUILD_INFO = "buildInfo"
        const val FCM_TOKEN = "fcm_token"
        const val NOTIFICATIONS_IDS = "notifications_ids"
    }

    fun init() {
        val appInfoJsonObj = JSONObject(readContentFromFile(context, "appInfo.json"))
        appInfo = AuthInfoHolder.buildAppInfo(appInfoJsonObj)
        device = DeviceInfo.build(context)
        team = AuthInfoHolder.buildTeam(appInfoJsonObj)
        language = LanguageInfo.build()
        buildInfo = BuildInfoHelper.build(appInfoJsonObj)
    }

    val prefs = defaultPrefs(context)

    val privatePrefs = customPrefs(context, PRIVATE_PREF_NAME)

    private val cookieManager = CookieManager.getInstance()

    // Application Info
    var appInfo: JSONObject
        get() = JSONObject(prefs[AUTH_APPLICATION] ?: "{}")
        set(value) {
            prefs[AUTH_APPLICATION] = value
        }

    // Application Info
    var buildInfo: JSONObject
        get() = JSONObject(prefs[BUILD_INFO] ?: "{}")
        set(value) {
            prefs[BUILD_INFO] = value
        }

    // Device Info
    var device: JSONObject
        get() = JSONObject(prefs[AUTH_DEVICE] ?: "{}")
        set(value) {
            prefs[AUTH_DEVICE] = value
        }

    // Team Info
    var team: JSONObject
        get() = JSONObject(prefs[AUTH_TEAM] ?: "{}")
        set(value) {
            prefs[AUTH_TEAM] = value
        }

    // Language Info
    var language: JSONObject
        get() = JSONObject(prefs[AUTH_LANGUAGE] ?: "{}")
        set(value) {
            prefs[AUTH_LANGUAGE] = value
        }

    var guestLogin: Boolean
        get() = prefs[GUEST_LOGIN] ?: false
        set(value) {
            prefs[GUEST_LOGIN] = value
        }

    var userInfo: String
        get() = prefs[USER_INFO] ?: "{}"
        set(value) {
            prefs[USER_INFO] = value
        }

    var remoteUrl: String
        get() = prefs[REMOTE_URL] ?: ""
        set(value) {
            prefs[REMOTE_URL] = value
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

    var parametersToSortWith: JSONObject
        get() = JSONObject(prefs[PARAMETERS_TO_SORT_WITH] ?: "{}")
        set(value) {
            prefs[PARAMETERS_TO_SORT_WITH] = value
        }

    // open for unit tests
    open var globalStamp: Int
        get() = prefs[GLOBAL_STAMP] ?: 0
        set(value) {
            prefs[GLOBAL_STAMP] = value
        }

    var deletedRecordsStamp: Int
        get() = prefs[DELETED_RECORDS_STAMP] ?: 0
        set(value) {
            prefs[DELETED_RECORDS_STAMP] = value
        }

    var cookies: String
        get() = prefs[COOKIES] ?: ""
        set(value) {
            prefs[COOKIES] = value
        }

    var lastLoginMail: String
        get() = prefs[LAST_LOGIN_MAIL] ?: ""
        set(value) {
            prefs[LAST_LOGIN_MAIL] = value
        }

    var crashLogSavedForLater: String
        get() = prefs[CRASH_LOG_SAVED_FOR_LATER] ?: ""
        set(value) {
            prefs[CRASH_LOG_SAVED_FOR_LATER] = value
        }

    var fcmToken: String
        get() = privatePrefs[FCM_TOKEN] ?: ""
        set(value) {
            privatePrefs[FCM_TOKEN] = value
        }

    var notificationsIds: String
        get() = privatePrefs[NOTIFICATIONS_IDS] ?: ""
        set(value) {
            privatePrefs[NOTIFICATIONS_IDS] = value
        }

    /**
     * Builds the request body for $authenticate request
     */
    fun buildAuthRequestBody(
        email: String,
        password: String,
        parameters: JSONObject = JSONObject()
    ): JSONObject {
        return JSONObject().apply {
            put(AUTH_EMAIL, email)
            put(AUTH_PASSWORD, password)
            put(AUTH_APPLICATION, appInfo)
            put(AUTH_DEVICE, device)
            put(AUTH_LANGUAGE, language)
            put(AUTH_PARAMETERS, parameters)
        }
    }

    /**
     * Gets the sessionToken from $authenticate request response
     */
    fun handleLoginInfo(authResponse: AuthResponse): Boolean {
        authResponse.userInfo?.let {
            UserInfoDateFormatter.storeUserInfo(it, this)
        }
        this.sessionId = authResponse.id ?: ""
        authResponse.token?.let {
            this.sessionToken = it
            return authResponse.success
        }
        return false
    }

    fun storeCookies(response: Response<ResponseBody>) {
        response.headers()[HeaderHelper.COOKIE_HEADER_KEY]?.let { cookies ->
            this.cookies = cookies
        }
    }

    fun injectCookies(url: String) {
        cookies.split(";").forEach { cookie ->
            cookieManager.setCookie(url, cookie)
        }
    }

    fun clearCookies() {
        cookieManager.removeSessionCookies(null)
        cookies = ""
    }

    fun addNotificationId(newId: Int) {
        notificationsIds =
            notificationsIds.split(",").toMutableList().apply { add(newId.toString()) }.toString()
    }

    fun removeNotificationId(currentId: Int) {
        if (currentId == -1) return
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(currentId)
        notificationsIds = getNotificationsIds().apply { remove(currentId.toString()) }.toString()
    }

    private fun getNotificationsIds(): MutableList<String> {
        return notificationsIds.split(",").toMutableList()
    }
}
