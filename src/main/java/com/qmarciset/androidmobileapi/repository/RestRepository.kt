package com.qmarciset.androidmobileapi.repository

import android.content.Context
import com.qmarciset.androidmobileapi.network.ApiClient
import com.qmarciset.androidmobileapi.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class RestRepository(private val tableName: String, context: Context) :
    BaseRestRepository {

    var disposable: CompositeDisposable = CompositeDisposable()
//    private val apiService = App.instance.apiService
    private val apiService = ApiClient.getClient(context = context).create(ApiService::class.java)

    override fun login(
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
    }

    override fun getAllFromApi(
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
