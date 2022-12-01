/*
 * Created by qmarciset on 13/7/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.content.Context
import android.webkit.CookieManager
import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.network.HeaderHelper
import com.qmobile.qmobileapi.utils.FileHelper.readContentFromFile
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.util.*

/**
 * Helper class to store authentication information into SharedPreferences
 */
open class SharedPreferencesHolder(val context: Context) {

    companion object : SingletonHolder<SharedPreferencesHolder, Context>(::SharedPreferencesHolder) {
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
        const val DEVICE_UUID = "device_uuid"

        const val GLOBAL_STAMP = "__GlobalStamp"
        const val DELETED_RECORDS_STAMP = "__Stamp"

        const val COOKIES = "Cookie"

        const val PRIVATE_PREF_NAME = "4D_QMOBILE_PRIVATE"

        const val USER_INFO = "userInfo"

        const val PARAMETERS_TO_SORT_WITH = "parameters_to_sort_with"

        const val LAST_LOGIN_MAIL = "last_login_mail"
    }

    fun init() {
        val appInfoJsonObj = JSONObject(readContentFromFile(context, "appInfo.json"))
        appInfo = AuthInfoHolder.buildAppInfo(appInfoJsonObj)
        device = DeviceInfo.build(context)
        team = AuthInfoHolder.buildTeam(appInfoJsonObj)
        language = LanguageInfo.build()
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

    /**
     * Builds the request body for $authenticate request
     */
    fun buildAuthRequestBody(email: String, password: String): JSONObject {
        return JSONObject().apply {
            put(AUTH_EMAIL, email)
            put(AUTH_PASSWORD, password)
            put(AUTH_APPLICATION, appInfo)
            put(AUTH_DEVICE, device)
            put(AUTH_TEAM, team)
            put(AUTH_LANGUAGE, language)
            put(AUTH_PARAMETERS, JSONObject())
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
}
