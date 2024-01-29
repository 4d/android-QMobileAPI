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
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

object UploadHelper {

    const val UPLOADED_METADATA_STRING = "uploaded"

    fun HashMap<String, *>.getBodies(activity: FragmentActivity): Map<String, Result<RequestBody>> {
        val contentResolver = activity.contentResolver
        return this.mapValues {
            val uri = FileHelper.repairUri(it.value)
            try {
                val stream = contentResolver.openInputStream(uri)
                if (stream != null)
                    return@mapValues Result.success(stream.readBytes().toRequestBody(APP_OCTET.toMediaTypeOrNull()).also { stream.close() })
                Timber.e("Failed to open stream on file $uri (null)")
               return@mapValues Result.failure(IOException("Failed to open stream on file $uri (null)"))
            }
            catch (exception: FileNotFoundException) {
                Timber.e("Failed to upload file due to file not found:" + exception.message)
                return@mapValues Result.failure(exception)
            }
            catch(exception: IOException) {
                Timber.e("Failed to upload file:" + exception.message)
                return@mapValues Result.failure(exception)
            }
            catch (exception: SecurityException) {
                Timber.e("Failed to upload file, no more access to it due to security issue:" + exception.message)
                return@mapValues Result.failure(exception)
            }
            catch (throwable: Throwable) { // it catch maybe two much...
                Timber.e("Failed to upload file, unknown error" + throwable.message)
                return@mapValues Result.failure(throwable)
            }
        }
    }
}
