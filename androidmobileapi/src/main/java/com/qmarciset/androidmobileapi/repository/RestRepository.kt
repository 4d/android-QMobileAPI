/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.repository

import com.qmarciset.androidmobileapi.network.ApiService
import com.qmarciset.androidmobileapi.utils.APP_JSON
import com.qmarciset.androidmobileapi.utils.UTF8_CHARSET
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class RestRepository(private val tableName: String, private val apiService: ApiService) {

    private var disposable: CompositeDisposable = CompositeDisposable()

    /**
     * Performs getEntities request
     */
    fun getEntities(
        tableName: String = this.tableName,
        filter: String,
        attributes: String? = null,
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            apiService.getEntities(
                dataClassName = tableName,
                filter = filter,
                attributes = attributes
            )
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

    /**
     * Performs getEntitiesExtendedAttributes request
     */
    fun getEntitiesExtendedAttributes(
        jsonRequestBody: JSONObject,
        tableName: String = this.tableName,
        filter: String,
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {

        val body = jsonRequestBody.toString()
            .toRequestBody("$APP_JSON; $UTF8_CHARSET".toMediaTypeOrNull())

        disposable.add(
            apiService.getEntitiesExtendedAttributes(
                body = body,
                dataClassName = tableName,
                filter = filter
            )
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

/*class RetryWithDelay2(private val MAX_RETRIES: Int, private val DELAY_DURATION_IN_SECONDS: Long)
    : Function1<Flowable<out Throwable>, Publisher<*>> {
    private var retryCount = 0

    override fun invoke(observable: Flowable<out Throwable>): Publisher<*> {
        if (++retryCount < MAX_RETRIES) {
            return observable.delay(DELAY_DURATION_IN_SECONDS, TimeUnit.SECONDS)
        } // Max retries hit. Just pass the error along.
        else {
            return observable.delay(0, TimeUnit.SECONDS)
        }
    }
}*/
/*fun <T> Single<T>.retryWithDelay(maxRetries: Int, retryDelayMillis: Int): Single<T> {
    var retryCount = 0

    return retryWhen { thObservable ->
        thObservable.flatMap { throwable ->
            if (++retryCount < maxRetries) {
                Flowable.timer(retryDelayMillis.toLong(), TimeUnit.MILLISECONDS)
            } else {
                Flowable.error(throwable)
            }
        }
    }
}*/
