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

open class RestRepository(private val tableName: String, private val apiService: ApiService) {

    var disposable: CompositeDisposable = CompositeDisposable()


    /*override fun login(
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            apiService.getInfo()
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
    }*/

    fun getAllFromApi(
        onResult: (isSuccess: Boolean, response: ResponseBody?, error: Any?) -> Unit
    ) {
        disposable.add(
            apiService.getEntities(tableName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<ResponseBody>>() {
                    override fun onSuccess(response: Response<ResponseBody>) {

                        if (response.isSuccessful) {
                            onResult(true, response.body(), null)
                        } else {
                            onResult(false, response.errorBody(), null)
                        }
                    }

                    override fun onError(e: Throwable) {
                        onResult(false, null, e)
                    }
                })
        )
    }
}
