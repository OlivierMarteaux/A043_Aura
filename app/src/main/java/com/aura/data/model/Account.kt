package com.aura.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a user account with an identifier, status, and current balance.
 *
 * @property id Unique identifier for the account.
 * @property main Indicates whether this account is the user's primary (main) account.
 * @property balance The current balance of the account in the relevant currency.
 */
@Serializable
data class Account (
    val id: String,
    val main: Boolean,
    val balance: Double
)