package com.aura.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    val granted: Boolean,
)