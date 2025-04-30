package com.aura.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Transfer (
    val sender: String,
    val recipient: String,
    val amount: Double,
)