/*
 * Created by Quentin Marciset on 5/11/2020.
 * 4D SAS
 * Copyright (c) 2020 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.google.gson.Gson
import com.qmobile.qmobileapi.model.entity.Entities
import org.junit.Assert
import org.junit.Test

class GsonExtTest {

    @Test
    fun `parse json string to type`() {
        val json =
            "{\"__entityModel\":\"Event\",\"__COUNT\":3,\"__SENT\":3,\"__FIRST\":0,\"__ENTITIES\":[{\"__KEY\":\"12\",\"__TIMESTAMP\":\"2017-03-06T14:47:38.001Z\",\"__STAMP\":16,\"id\":12,\"count\":605,\"timeStamp\":\"6!3!2017\",\"title\":\"Twelfth Event\"},{\"__KEY\":\"14\",\"__TIMESTAMP\":\"2017-03-06T14:48:26.526Z\",\"__STAMP\":14,\"id\":14,\"count\":1,\"timeStamp\":\"6!3!2017\",\"title\":\"Fourteenth Event\"},{\"__KEY\":\"15\",\"__TIMESTAMP\":\"2017-03-06T15:03:47.314Z\",\"__STAMP\":7,\"id\":15,\"count\":186,\"timeStamp\":null,\"title\":\"Fifteenth Event\"}]}"

        val gson = Gson()
        val entities = gson.parseJsonToType<Entities<EventApiTest>>(json)
        Assert.assertEquals(3, entities?.__COUNT)
        Assert.assertEquals("Event", entities?.__entityModel)
    }

    @Test
    fun `parse empty json`() {
        val json = ""
        val gson = Gson()
        val entities = gson.parseJsonToType<Any>(json)
        Assert.assertNull(entities)
    }

    @Test
    fun `parse null json`() {
        val json: String? = null
        val gson = Gson()
        val entities = gson.parseJsonToType<Any>(json)
        Assert.assertNull(entities)
    }
}
