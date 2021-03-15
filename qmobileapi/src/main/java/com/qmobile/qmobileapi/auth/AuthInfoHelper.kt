/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.auth

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.qmobile.qmobileapi.model.auth.AuthResponse
import com.qmobile.qmobileapi.model.queries.Queries
import com.qmobile.qmobileapi.utils.SingletonHolder
import com.qmobile.qmobileapi.utils.customPrefs
import com.qmobile.qmobileapi.utils.defaultPrefs
import com.qmobile.qmobileapi.utils.get
import com.qmobile.qmobileapi.utils.parseJsonToType
import com.qmobile.qmobileapi.utils.set
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.UUID

/**
 * Helper class to store authentication information into SharedPreferences
 */
open class AuthInfoHelper(val context: Context) {

    companion object : SingletonHolder<AuthInfoHelper, Context>(::AuthInfoHelper) {
        const val AUTH_EMAIL = "email"
        const val AUTH_PASSWORD = "password"
        const val AUTH_APPLICATION = "application"
        const val AUTH_DEVICE = "device"
        const val AUTH_TEAM = "team"
        const val AUTH_LANGUAGE = "language"
        const val AUTH_PARAMETERS = "parameters"

        const val GUEST_LOGIN = "guest_login"
        const val EMBEDDED_DATA = "embedded_data"
        const val REMOTE_URL = "remote_url"
        const val SESSION_ID = "session_id"
        const val SESSION_TOKEN = "session_token"
        const val DEVICE_UUID = "device_uuid"

        const val QUERY_PREFIX = "queries_"
        const val PROPERTIES_PREFIX = "properties_"

        const val GLOBAL_STAMP = "__GlobalStamp"
        const val DELETED_RECORDS_STAMP = "__Stamp"

        const val COOKIE = "Cookie"

        const val PRIVATE_PREF_NAME = "4D_QMOBILE_PRIVATE"

        const val USER_INFO = "userInfo"
    }

    val prefs =
        defaultPrefs(context)

    val privatePrefs =
        customPrefs(context, PRIVATE_PREF_NAME)

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

    var userInfo: String? = privatePrefs[USER_INFO]

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

    var embeddedData: Boolean
        get() = prefs[EMBEDDED_DATA] ?: false
        set(value) {
            prefs[EMBEDDED_DATA] = value
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

    // open for unit tests
    open var globalStamp: Int
        get() {
            val gs = prefs[GLOBAL_STAMP] ?: -1
            return if (gs == -1) 0 else gs
        }
        set(value) {
            prefs[GLOBAL_STAMP] = value
        }

    var deletedRecordsStamp: Int
        get() {
            val drs = prefs[DELETED_RECORDS_STAMP] ?: -1
            return if (drs == -1) 0 else drs
        }
        set(value) {
            prefs[DELETED_RECORDS_STAMP] = value
        }

    var cookie: String
        get() = prefs[COOKIE] ?: ""
        set(value) {
            prefs[COOKIE] = value
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
        setUserInfo(authResponse.userInfo!!)
        this.sessionId = authResponse.id ?: ""
        authResponse.token?.let {
            this.sessionToken = it
            return true
        }
        return false
    }

    // Store UserInfo
    @SuppressLint("SimpleDateFormat")
    fun setUserInfo(userInfo: JsonObject) {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(
            userInfo.get("date").toString().split("\"")[1]
        )
        val newDateFormat = SimpleDateFormat("yy!MM!dd").format(date!!)
        userInfo.addProperty("date", newDateFormat)
        privatePrefs[USER_INFO] = userInfo.toString()
    }

    // Table queries
    fun getQuery(tableName: String): String = prefs["$QUERY_PREFIX$tableName"] ?: ""

    fun setQueries(queriesJSONObject: JSONObject) {
        val queriesObject = Gson().parseJsonToType<Queries>(queriesJSONObject.toString())
        queriesObject?.let {
            for (query in queriesObject.queries) {
                if (query.tableName.isNullOrEmpty().not() && query.value.isNullOrEmpty().not())
                    prefs["$QUERY_PREFIX${query.tableName}"] = query.value
            }
        }
    }

    // Table properties
    fun getProperties(tableName: String): String = prefs["$PROPERTIES_PREFIX$tableName"] ?: ""

    fun setProperties(tableName: String, properties: String) {
        prefs["$PROPERTIES_PREFIX$tableName"] = properties
    }
}
