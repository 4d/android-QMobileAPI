/*
 * Created by qmarciset on 18/7/2022.
 * 4D SAS
 * Copyright (c) 2022 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import androidx.fragment.app.FragmentActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object UploadHelper {

    const val UPLOADED_METADATA_STRING = "uploaded"

    fun HashMap<String, *>.getBodies(activity: FragmentActivity?): Map<String, RequestBody?> = this.mapValues {
        val uri = FileHelper.repairUri(it.value.toString())
        val stream = activity?.contentResolver?.openInputStream(uri)
        stream?.readBytes()?.toRequestBody(APP_OCTET.toMediaTypeOrNull())
    }
}
