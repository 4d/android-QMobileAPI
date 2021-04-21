/*
 * Created by Quentin Marciset on 21/4/2021.
 * 4D SAS
 * Copyright (c) 2021 Quentin Marciset. All rights reserved.
 */

package com.qmobile.qmobileapi.utils

import com.dslplatform.json.DslJson
import com.dslplatform.json.runtime.Settings

object DSL {
    private var json: DslJson<Any>? = null
    fun JSON(): DslJson<Any> {
        var tmp = json
        if (tmp != null) return tmp
        // during initialization ServiceLoader.load should pick up services registered into META-INF/services
        // this doesn't really work on Android so DslJson will fallback to default generated class name
        // "dsl_json_Annotation_Processor_External_Serialization" and try to initialize it manually
        tmp = DslJson(Settings.withRuntime<Any>().includeServiceLoader().allowArrayFormat(true))
        json = tmp
        return tmp
    }
}
