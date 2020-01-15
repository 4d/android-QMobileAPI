package com.qmarciset.androidmobileapi.auth

import android.content.Context
import com.qmarciset.androidmobileapi.network.ApiService
import com.qmarciset.androidmobileapi.repository.AuthRepository
import com.qmarciset.androidmobileapi.utils.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class AuthenticationHelper(
    private val apiService: ApiService,
    private val context: Context
) {

    fun loginIfRequired() {
        if (CookieHelper.getCookieFromPref(context) == null) {  //TODO : CHECK GUESTLOGIN COMME DANS IOS
            realLogin()
        } else {
            // else login form must be displayed
        }
    }

    private fun buildAuthRequestBody(email: String = "", password: String = ""): JSONObject {
        val authInfoHelper = AuthInfoHelper(context)
        return JSONObject().apply {
            put(AUTH_EMAIL, email)
            put(AUTH_PASSWORD, password)
            put(AUTH_APPLICATION, authInfoHelper.appInfo)
            put(AUTH_DEVICE, authInfoHelper.device)
            put(AUTH_TEAM, authInfoHelper.team)
            put(AUTH_LANGUAGE, authInfoHelper.language)
            put(AUTH_PARAMETERS, JSONObject())
        }
    }

    private fun realLogin() {
        val authRepository = AuthRepository(apiService)
        val authRequest = buildAuthRequestBody()
        authRepository.authenticate(authRequest) { isSuccess, response, error ->
            if (isSuccess) {
                response?.let {
                    treatLoginInfo(it)
                }
            } else {
                // TODO : check error from response or from error
                Timber.e("Error: $error")
            }
        }
    }

    /*fun loginIfRequired() {
        if (CookieHelper.getCookieFromPref(context) == null) {
            Timber.d("Couldn't find cookie in pref, must perform login request")
            println("going to login now")
            realLogin()
        } else {
            println("NOT going to login")
            Timber.d("Cookie found in pref")
        }
    }*/

    /*private fun login() {
        restRepository.login { isSuccess, response, error ->
            if (isSuccess) {
                response?.let {
                    treatLoginInfo(it)
                }
            } else {
                // TODO : check error from response or from error
                Timber.e("Error: $error")
            }
        }
    }*/

    private fun treatLoginInfo(response: Response<ResponseBody>): Boolean {
        val cookieString = CookieHelper.buildCookieString(response.headers())
        cookieString?.let {
            CookieHelper.saveCookieInPref(context, cookieString)
            return true
        }
        return false
    }
}
