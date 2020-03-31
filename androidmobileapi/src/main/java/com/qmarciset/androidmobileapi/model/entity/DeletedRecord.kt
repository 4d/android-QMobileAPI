/*
 * Created by Quentin Marciset on 27/3/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmarciset.androidmobileapi.model.entity

import com.google.gson.Gson
import com.qmarciset.androidmobileapi.auth.AuthInfoHelper
import com.qmarciset.androidmobileapi.repository.RestRepository
import com.qmarciset.androidmobileapi.utils.DELETED_RECORDS
import com.qmarciset.androidmobileapi.utils.RequestErrorHelper
import com.qmarciset.androidmobileapi.utils.parseJsonToType
import timber.log.Timber

@Suppress("ConstructorParameterNaming")
data class DeletedRecord(
    val __KEY: String,
    val __TIMESTAMP: String? = null,
    val __STAMP: Int? = null,
    val __Stamp: Int? = null,
    val __PrimaryKey: String? = null,
    val __TableNumber: Int? = null,
    val __TableName: String? = null
) {

    companion object {
        /**
         * Returns predicate for requests with __Stamp
         */
        private fun buildStampPredicate(globalStamp: Int): String {
            return "\"__Stamp >= $globalStamp\""
        }

        private fun decodeDeletedRecords(
            gson: Gson,
            entities: Entities?,
            onResult: (deletedRecordList: List<DeletedRecord>) -> Unit
        ) {
            val deletedRecordList: List<DeletedRecord>? = gson.parseJsonToType(entities?.__ENTITIES)
            deletedRecordList?.let {
                onResult(deletedRecordList)
            }
        }

        fun getDeletedRecords(
            gson: Gson,
            restRepository: RestRepository,
            authInfoHelper: AuthInfoHelper,
            onResult: (deletedRecordList: List<DeletedRecord>) -> Unit
        ) {
            val predicate = buildStampPredicate(authInfoHelper.deletedRecordsStamp)
            Timber.d("Performing data request, with predicate $predicate")

            restRepository.getMoreRecentEntities(
                tableName = DELETED_RECORDS,
                predicate = predicate
            ) { isSuccess, response, error ->
                if (isSuccess) {
                    response?.body()?.let {
                        Entities.decodeEntities(gson, it) { entities ->

                            authInfoHelper.deletedRecordsStamp = authInfoHelper.globalStamp

                            decodeDeletedRecords(gson, entities) { deletedRecords ->
                                onResult(deletedRecords)
                            }
                        }
                    }
                } else {
                    RequestErrorHelper.handleError(error)
                }
            }
        }
    }
}
