package com.qmarciset.androidmobileapi.model.info

data class SessionInfo(
    val expiration: String? = null,
    val lifeTime: Int? = null,
    val sessionID: String? = null,
    val userID: String? = null,
    val userName: String? = null
)
