/*
 * Created by Quentin Marciset on 27/3/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.model.entity

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
        fun buildStampPredicate(globalStamp: Int): String {
            return "\"$STAMP_PROPERTY >= $globalStamp\""
        }
    }
}
