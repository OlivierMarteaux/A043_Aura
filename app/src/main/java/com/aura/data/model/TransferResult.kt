package com.aura.data.model

import kotlinx.serialization.Serializable

/**
 * Represents the outcome of a transfer operation.
 *
 * @property result Indicates whether the transfer was successful (`true`) or failed (`false`).
 */
@Serializable
data class TransferResult (
    val result: Boolean
)