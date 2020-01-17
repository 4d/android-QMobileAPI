package com.qmarciset.androidmobileapi.auth

import android.content.Context
import com.google.gson.Gson
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper.Companion.AUTH_APPLICATION
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper.Companion.AUTH_DEVICE
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper.Companion.AUTH_EMAIL
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper.Companion.AUTH_LANGUAGE
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper.Companion.AUTH_PARAMETERS
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper.Companion.AUTH_PASSWORD
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper.Companion.AUTH_TEAM
import com.qmarciset.androidmobileapi.model.auth.AuthResponse
import com.qmarciset.androidmobileapi.network.ApiService
import com.qmarciset.androidmobileapi.repository.AuthRepository
import com.qmarciset.androidmobileapi.utils.parseJsonToType
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class AuthenticationHelper(
    private val apiService: ApiService,
    context: Context
) {

    private val authInfoHelper = AuthInfoHelper.getInstance(context)

    fun loginIfRequired() {
        if (authInfoHelper.sessionToken.isEmpty()) {
            Timber.d("login is required")
            login()
        } else {
            Timber.d("login is NOT required")
        }
    }

    private fun buildAuthRequestBody(email: String, password: String): JSONObject {
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

    fun login(email: String = "", password: String = "") {
        val authRepository = AuthRepository(apiService)
        val authRequest = buildAuthRequestBody(email, password)
        authRepository.authenticate(authRequest) { isSuccess, response, error ->
            Timber.d("authenticate returned : isSuccess = $isSuccess, response = $response, error = $error")
            if (isSuccess) {
                response?.let {
                    if (treatLoginInfo(it)) {

                    }
                }
            } else {
                // TODO : check error from response or from error
                Timber.e("Error: $error")
            }
        }
    }

    private fun treatLoginInfo(response: Response<ResponseBody>): Boolean {

        val responseBody = response.body()
        val json = responseBody?.string()
        val authResponse: AuthResponse? = Gson().parseJsonToType<AuthResponse>(json)
        authResponse?.let {
            authInfoHelper.sessionId = authResponse.id ?: ""
            authInfoHelper.sessionToken = authResponse.token ?: ""
            return true
        }
        return false
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

    /*private fun treatLoginInfo(response: Response<ResponseBody>): Boolean {
        val cookieString = CookieHelper.buildCookieString(response.headers())
        cookieString?.let {
            CookieHelper.saveCookieInPref(context, cookieString)
            return true
        }
        return false
    }*/
}
