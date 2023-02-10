/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.repository

import com.qmobile.qmobileapi.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class PushRepository(private val apiService: ApiService) {

    var disposable: CompositeDisposable = CompositeDisposable()

    /**
     * Sends userInfo with device token for push notifications
     */
    fun sendUserInfo(
        userInfo: MutableMap<String, Any>,
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    ) {
        disposable.add(
            apiService.sendUserInfo(userInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(DisposableSingleObserver(onResult))
        )
    }
}
