package com.qmarciset.androidmobileapi.repository

import com.qmarciset.androidmobileapi.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    var disposable: CompositeDisposable = CompositeDisposable()

    fun authenticate(
        jsonRequest: JSONObject,
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        val body = jsonRequest.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        disposable.add(
            apiService.authenticate(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<ResponseBody>>() {
                    override fun onSuccess(response: Response<ResponseBody>) {

                        if (response.isSuccessful) {
                            onResult(true, response, null)
                        } else {
                            onResult(false, response, null)
                        }
                    }

                    override fun onError(e: Throwable) {
                        onResult(false, null, e)
                    }
                })
        )
    }
}
