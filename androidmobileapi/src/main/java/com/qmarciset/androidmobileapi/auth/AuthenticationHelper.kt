package com.qmarciset.androidmobileapi.auth

import android.content.Context
import com.qmarciset.androidmobileapi.repository.RestRepository
import com.qmarciset.androidmobileapi.utils.CookieHelper
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber

class AuthenticationHelper(
    private val restRepository: RestRepository,
    private val context: Context
) {

    fun loginIfRequired() {
        if (CookieHelper.getCookieFromPref(context) == null) {
            Timber.d("Couldn't find cookie in pref, must perform login request")
            println("going to login now")
            login()
        } else {
            println("NOT going to login")
            Timber.d("Cookie found in pref")
        }
    }

    private fun login() {
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
    }

    private fun treatLoginInfo(response: Response<ResponseBody>): Boolean {
        val cookieString = CookieHelper.buildCookieString(response.headers())
        cookieString?.let {
            CookieHelper.saveCookieInPref(context, cookieString)
            return true
        }
        return false
    }
}
