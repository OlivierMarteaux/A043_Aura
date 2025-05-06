package com.aura.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a monetary transfer between two accounts.
 *
 * @property sender The unique identifier of the account sending the funds.
 * @property recipient The unique identifier of the account receiving the funds.
 * @property amount The amount of money to be transferred.
 */
@Serializable
data class Transfer (
    val sender: String,
    val recipient: String,
    val amount: Double,
)