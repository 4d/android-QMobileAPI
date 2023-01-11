/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.repository

import com.qmobile.qmobileapi.network.FeedbackApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class FeedbackRepository(private val feedbackApiService: FeedbackApiService) {

    var disposable: CompositeDisposable = CompositeDisposable()

    fun checkAccessibility(
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            feedbackApiService.checkAccessibility()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DisposableSingleObserver(onResult))
        )
    }

    fun send(
        partMap: MutableMap<String, RequestBody>,
        filePart: MultipartBody.Part?,
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            feedbackApiService.send(
                partMap = partMap,
                file = filePart
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DisposableSingleObserver(onResult))
        )
    }
}
