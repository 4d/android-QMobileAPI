/*
 * Created by qmarciset on 13/7/2021.
 * 4D SAS
 * Copyright (c) 2021 qmarciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.IOException

object FileHelper {

    @SuppressLint("LogNotTimber")
    fun readContentFromFile(context: Context, fileName: String): String =
        try {
            context.assets.open(fileName).bufferedReader().use {
                it.readText()
            }
        } catch (e: IOException) {
            Log.e("FileHelper", "Missing file \"$fileName\" in assets")
            "{}"
        }

    fun listAssetFiles(context: Context, path: String): List<String> {
        val result = ArrayList<String>()
        context.assets.list(path)?.forEach { file ->
            val innerFiles = listAssetFiles(context, "$path/$file")
            if (innerFiles.isNotEmpty()) {
                result.addAll(innerFiles)
            } else {
                result.add("$path/$file")
            }
        }
        return result
    }

    fun repairUri(uriString: String): Uri {
        val uriBuilder: String = when {
            uriString.startsWith("/") -> "file://$uriString"
            uriString.startsWith("content://") -> uriString
            !uriString.startsWith("file:///") -> "file:///$uriString"
            else -> uriString
        }
        return Uri.parse(uriBuilder)
    }
}
