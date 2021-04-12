/*
 * Created by Quentin Marciset on 12/4/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.repository

import com.qmobile.qmobileapi.network.AccessibilityApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class AccessibilityRepository(private val accessibilityApiService: AccessibilityApiService) {

    var disposable: CompositeDisposable = CompositeDisposable()

    fun checkAccessibility(
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            accessibilityApiService.checkAccessibility()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DisposableSingleObserver(onResult))
        )
    }
}
