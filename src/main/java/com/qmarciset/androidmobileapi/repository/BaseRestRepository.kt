package com.qmarciset.androidmobileapi.repository

import okhttp3.ResponseBody
import retrofit2.Response

interface BaseRestRepository {

    fun login(
        onResult: (isSuccess: Boolean, response: Response<ResponseBody>?, error: Any?) -> Unit
    )

    fun getAllFromApi(onResult: (isSuccess: Boolean, response: ResponseBody?, error: Any?) -> Unit)
}
