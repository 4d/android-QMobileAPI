/*
 * Created by Quentin Marciset on 29/6/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.repository

import io.reactivex.observers.DisposableSingleObserver
import okhttp3.ResponseBody
import retrofit2.Response

class DisposableSingleObserver(
    private val onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
) : DisposableSingleObserver<Response<ResponseBody>>() {
    override fun onSuccess(response: Response<ResponseBody>) {

        if (response.isSuccessful) {
            onResult(true, response, null)
        } else {
            onResult(false, null, response)
        }
    }

    override fun onError(error: Throwable) {
        onResult(false, null, error)
    }
}
