package com.qmarciset.androidmobileapi.repository

import com.qmarciset.androidmobileapi.model.auth.AuthResponse
import com.qmarciset.androidmobileapi.network.LoginApiService
import com.qmarciset.androidmobileapi.utils.MAX_LOGIN_RETRY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class AuthRepository(private val loginApiService: LoginApiService) {

    var disposable: CompositeDisposable = CompositeDisposable()

    /**
     * Performs synchronous $authenticate request
     */
    fun syncAuthenticate(jsonRequest: JSONObject): AuthResponse? {
        val body = jsonRequest.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val authResponse = loginApiService.syncAuthenticate(body).execute().body()
        authResponse?.let {
            if (authResponse.success) {
                return authResponse
            }
        }
        return null
    }

    /**
     * Performs asynchronous $authenticate request
     */
    fun authenticate(
        jsonRequest: JSONObject,
        shouldRetryOnError: Boolean,
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        val body = jsonRequest.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        disposable.add(
            loginApiService.authenticate(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry { integer, _ ->
                    // Retries authentication MAX_LOGIN_RETRY times
                    Timber.d("Retrying authenticate automatically")
                    integer <= MAX_LOGIN_RETRY && shouldRetryOnError
                }
                .subscribeWith(object : DisposableSingleObserver<Response<ResponseBody>>() {
                    override fun onSuccess(response: Response<ResponseBody>) {

                        if (response.isSuccessful) {
                            onResult(true, response, null)
                        } else {
                            onResult(false, null, response)
                        }
                    }

                    override fun onError(e: Throwable) {
                        onResult(false, null, e)
                    }
                })
        )
    }

    /**
     * Performs synchronous $logout request
     */
    fun syncLogout(): Boolean {
        val logoutResponse = loginApiService.syncLogout().execute().body()
        return logoutResponse?.ok ?: false
    }

    /**
     * Performs asynchronous $logout request
     */
    fun logout(
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            loginApiService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<ResponseBody>>() {
                    override fun onSuccess(response: Response<ResponseBody>) {

                        if (response.isSuccessful) {
                            onResult(true, response, null)
                        } else {
                            onResult(false, null, response)
                        }
                    }

                    override fun onError(e: Throwable) {
                        onResult(false, null, e)
                    }
                })
        )
    }
}
