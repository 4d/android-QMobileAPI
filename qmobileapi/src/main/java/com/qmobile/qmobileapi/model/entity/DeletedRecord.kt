/*
 * Created by Quentin Marciset on 27/3/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.entity

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.qmobile.qmobileapi.auth.AuthInfoHelper
import com.qmobile.qmobileapi.repository.RestRepository
import com.qmobile.qmobileapi.utils.DELETED_RECORDS
import com.qmobile.qmobileapi.utils.RequestErrorHelper
import timber.log.Timber

@Suppress("ConstructorParameterNaming")
data class DeletedRecord(
    override val __KEY: String,
    override val __TIMESTAMP: String? = null,
    override val __STAMP: Int? = null,
    override val __GlobalStamp: Int? = null,
    val __Stamp: Int? = null,
    val __PrimaryKey: String? = null,
    val __TableNumber: Int? = null,
    val __TableName: String? = null
) : EntityModel {

    companion object {

        private const val STAMP_PROPERTY = "__Stamp"

        /**
         * Returns predicate for requests with __Stamp
         */
        private fun buildStampPredicate(globalStamp: Int): String {
            return "\"$STAMP_PROPERTY >= $globalStamp\""
        }

        fun getDeletedRecords(
            gson: Gson,
            restRepository: RestRepository,
            authInfoHelper: AuthInfoHelper,
            toastMessage: MutableLiveData<String>,
            onResult: (entities: Entities<DeletedRecord>?) -> Unit
        ) {
            val predicate = buildStampPredicate(authInfoHelper.deletedRecordsStamp)
            Timber.d("Performing data request, with predicate $predicate")

            restRepository.getEntities(
                tableName = DELETED_RECORDS,
                filter = predicate
            ) { isSuccess, response, error ->
                if (isSuccess) {
                    response?.body()?.let {
                        Entities.decodeEntities<DeletedRecord>(gson, it) { entities ->

                            authInfoHelper.deletedRecordsStamp = authInfoHelper.globalStamp

                            onResult(entities)
                        }
                    }
                } else {
                    RequestErrorHelper.handleError(error, toastMessage)
                }
            }
        }
    }
}
