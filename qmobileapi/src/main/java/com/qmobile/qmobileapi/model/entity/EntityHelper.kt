package com.qmobile.qmobileapi.model.entity

import java.util.Locale
import kotlin.reflect.KProperty1

class EntityHelper private constructor() {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
            val property = instance::class.members
                .first {
                    it.name.lowercase(Locale.getDefault()) == propertyName.lowercase(Locale.getDefault())
                } as KProperty1<Any, *>
            return property.get(instance) as R
        }

        fun readInstanceProperty(instance: Any, propertyName: String): String {
            val property = instance::class.members
                .first {
                    it.name.lowercase(Locale.getDefault()) == propertyName.lowercase(Locale.getDefault())
                } as KProperty1<Any, *>
            return property.get(instance).toString()
        }
    }
}
