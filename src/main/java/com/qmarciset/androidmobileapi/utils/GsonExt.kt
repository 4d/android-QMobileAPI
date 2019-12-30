package com.qmarciset.androidmobileapi.utils

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

inline fun <reified T : Any> Gson.parseJsonToType(json: String?): T? {
    if (json.isNullOrEmpty())
        return null
    val type = object : TypeToken<T>() {}.type
    return this.fromJson<T>(json, type)
}

inline fun <reified T : Any> Gson.parseJsonToType(json: JsonElement?): T? {
    if (json == null)
        return null
    val type = object : TypeToken<T>() {}.type
    return this.fromJson<T>(json, type)
}
