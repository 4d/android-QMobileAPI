/*
 * Created by htemanni on 13/9/2021.
 * 4D SAS
 * Copyright (c) 2021 htemanni. All rights reserved.
 */

package com.qmobile.qmobileapi.model.action

data class ActionContent(val context: Map<String, Any>, val parameters: Map<String, Any>?, val metadata: Metadata)