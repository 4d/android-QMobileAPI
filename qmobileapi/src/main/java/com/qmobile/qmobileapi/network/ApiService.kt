/*
 * Created by Quentin Marciset on 7/2/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.network

import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Suppress("TooManyFunctions")
interface ApiService {

    /*
     * CATALOG ACTIONS
     */

    /**
     * Returns a list of the datastore classes in your project along with two URIs: one to access
     * the information about its structure and one to retrieve the data in the datastore class
     */
    @GET("\$catalog")
    fun getCatalog(): Single<Response<ResponseBody>>

    /**
     * Returns information about all of your project's datastore classes and their attributes
     */
    @GET("\$catalog/\$all")
    fun getAllDataClasses(): Single<Response<ResponseBody>>

    /**
     * Returns information about a datastore class and its attributes
     */
    @GET("\$catalog/{dataClassName}")
    fun getDataClass(@Path("dataClassName") dataClassName: String): Single<Response<ResponseBody>>

    /*
     * INFO ACTION
     */

    /**
     * Returns information about the entity sets currently stored in 4D Server's cache as well as
     * user sessions
     */
    @GET("\$info")
    fun getInfo(): Single<Response<ResponseBody>>

    /*
     * DATACLASS ACTIONS
     */

    /**
     * Returns the data for the specific entity defined by the datastore class's primary key,
     * e.g., Company(22) or Company("IT0911AB2200")
     * [dataClassName] table name
     * [key] entity primary key
     * [attributes] specifies the attributes from table and related tables to receive
     */
    @GET("{dataClassName}({key})")
    fun getEntity(
        @Path("dataClassName") dataClassName: String,
        @Path("key") key: String,
        @Query("\$attributes", encoded = true) attributes: String? = null
    ): Single<Response<ResponseBody>>

    /**
     * Returns all the data (by default the first 100 entities) for a specific datastore class
     * (e.g., Company)
     * [dataClassName] table name
     * [filter] specifies a filter on entity set
     * [attributes] specifies the related attributes of the table to return
     */
    @GET("{dataClassName}")
    fun getEntities(
        @Path("dataClassName") dataClassName: String,
        @Query("\$filter", encoded = true) filter: String? = null,
        @Query("\$attributes", encoded = true) attributes: String? = null,
        @Query("\$limit", encoded = true) limit: Int = 100000
    ): Single<Response<ResponseBody>>

    /**
     * Returns all the data (by default the first 100 entities) for a specific datastore class
     * (e.g., Company)
     * [body] contains data such as wanted attributes, or queries on related attributes
     * [dataClassName] table name
     * [filter] specifies a filter on entity set
     * [extendedAttributes] true, to ensure that we get filtered related entities, and reading
     * request body
     */
    @POST("{dataClassName}")
    fun getEntitiesExtendedAttributes(
        @Body body: RequestBody,
        @Path("dataClassName") dataClassName: String,
        @Query("\$filter", encoded = true) filter: String? = null,
        @Query("\$extendedAttributes", encoded = true) extendedAttributes: Boolean = true,
        @Query("\$limit", encoded = true) limit: Int = 100000
    ): Single<Response<ResponseBody>>

    /**
     * Returns all the data (by default the first 100 entities) for a specific datastore class
     * (e.g., Company)
     * [body] contains data such as wanted attributes, or queries on related attributes
     * [dataClassName] table name
     * [filter] specifies a filter on entity set
     * [params] specifies a params on entity set
     * [extendedAttributes] true, to ensure that we get filtered related entities, and reading
     * request body
     */
    @POST("{dataClassName}")
    fun getEntitiesExtendedAttributes(
        @Body body: RequestBody,
        @Path("dataClassName") dataClassName: String,
        @Query("\$filter", encoded = true) filter: String? = null,
        @Query("\$extendedAttributes", encoded = true) extendedAttributes: Boolean = true,
        @Query("\$params", encoded = true) params: String? = null,
        @Query("\$skip", encoded = true) skip: Int? = null,
        @Query("\$limit", encoded = true) limit: Int = 100000
    ): Single<Response<ResponseBody>>

    /**
     * Execute 4D code from Android app
     */
    @POST("\$action/{actionName}")
    fun sendAction(
        @Path("actionName") actionName: String,
        @Body body: MutableMap<String, Any>
    ): Single<Response<ResponseBody>>

    /**
     * Uploads an image on the 4D server
     */
    @POST("\$upload")
    fun uploadImage(
        @Header("Content-Type") mime: String = "image/png",
        @Query("\$rawPict", encoded = true) rawPict: Boolean = true,
        @Body body: RequestBody
    ): Single<Response<ResponseBody>>

    /**
     * Sends userInfo with device token for push notifications
     */
    @POST("\$userInfo")
    fun sendUserInfo(
        @Body body: MutableMap<String, Any>
    ): Single<Response<ResponseBody>>
}
