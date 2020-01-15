package com.qmarciset.androidmobileapi.network

import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /*
     * AUTHENTICATION
     */

    /*
    * TODO: add description
    * */
    @POST("\$authenticate")
    fun authenticate(@Body body: RequestBody): Single<Response<ResponseBody>>

    /*
     * CATALOG ACTIONS
     */

    /*
    * Returns a list of the datastore classes in your project along with two URIs: one to access
    * the information about its structure and one to retrieve the data in the datastore class
    * */
    @GET("\$catalog")
    fun getCatalog(): Single<Response<ResponseBody>>

    /*
    * Returns information about all of your project's datastore classes and their attributes
    * */
    @GET("\$catalog/\$all")
    fun getAllDataClasses(): Single<Response<ResponseBody>>

    /*
    * Returns information about a datastore class and its attributes
    * */
    @GET("\$catalog/{dataClassName}")
    fun getDataClass(@Path("dataClassName") dataClassName: String): Single<Response<ResponseBody>>

    /*
     * INFO ACTION
     */

    /*
       * Returns information about the entity sets currently stored in 4D Server's cache as well as
       * user sessions
       * */
    @GET("\$info")
    fun getInfo(): Single<Response<ResponseBody>>

    /*
     * DATACLASS ACTIONS
     */

    /*
    * Returns the data for the specific entity defined by the datastore class's primary key,
    * e.g., Company(22) or Company("IT0911AB2200")
    * */
    @GET("{dataClassName}({key})")
    fun getEntity(
        @Path("dataClassName") dataClassName: String,
        @Path("key") key: String
    ): Single<Response<ResponseBody>>

    /*
    * Applies the previous request but only returns specified attributes values
    * */
    @GET("{dataClassName}({key})/{attributes}")
    fun getEntityWithAttributes(
        @Path("dataClassName") dataClassName: String,
        @Path("key") key: String,
        @Path("attributes") attributes: String
    ): Single<Response<ResponseBody>>

    /*
    * Returns all the data (by default the first 100 entities) for a specific datastore class
    * (e.g., Company)
    * */
    @GET("{dataClassName}")
    fun getEntities(@Path("dataClassName") dataClassName: String): Single<Response<ResponseBody>>

    /*
    * Applies the previous request but only returns specified attributes values
    * */
    @GET("{dataClassName}/{attributes}")
    fun getEntitiesWithAttributes(
        @Path("dataClassName") dataClassName: String,
        @Path("attributes") attributes: String
    ): Single<Response<ResponseBody>>

    /*
    * Filters the data in a datastore class or method (e.g., $filter="firstName!='' AND salary>30000")
    * */
    @GET("{dataClassName}/")
    fun getEntitiesFiltered(
        @Path("dataClassName") dataClassName: String,
        @Query("\$filter", encoded = true) predicate: String
    ): Single<Response<ResponseBody>>

    /*
    * Applies the previous request but only returns specified attributes values
    * */
    @GET("{dataClassName}/{attributes}/")
    fun getEntitiesFilteredWithAttributes(
        @Path("dataClassName") dataClassName: String,
        @Path("attributes") attributes: String,
        @Query("\$filter", encoded = true) predicate: String
    ): Single<Response<ResponseBody>>
}
