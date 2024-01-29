/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.repository

import com.qmobile.qmobileapi.network.ApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class ActionRepository(private val apiService: ApiService) {

    var disposable: CompositeDisposable = CompositeDisposable()

    /**
     * Performs $action request
     */
    fun sendAction(
        actionName: String,
        actionContent: MutableMap<String, Any>,
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            apiService.sendAction(
                actionName,
                actionContent
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DisposableSingleObserver(onResult))
        )
    }

    /**
     * Performs $upload request
     */
    fun uploadImage(
        imagesToUpload: Map<String, Result<RequestBody>>,
        onImageUploaded:
            (isSuccess: Boolean, parameterName: String, response: Response<ResponseBody>?, error: Throwable?) -> Unit,
        onAllUploadFinished: () -> Unit
    ) {
        disposable.add(
            Observable.just(imagesToUpload.entries)
                .flatMapIterable { entries ->
                    entries
                }
                .concatMapSingle {
                    val parameterName = it.key
                    val result = it.value
                    val requestBody = result.getOrThrow() // we have postponed error here to be managed by listeners
                    apiService.uploadImage(body = requestBody)
                        .map { response ->
                            parameterName to response
                        }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    onAllUploadFinished()
                }
                .subscribe(
                    { onImageUploaded(it.second.isSuccessful, it.first, it.second, null) },
                    { onImageUploaded(false, "", null, it) }
                )
        )
    }
}
