package com.aura.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing the response received from the login API.
 *
 * The server returns this object to indicate whether the login credentials
 * were accepted.
 *
 * @property granted A boolean flag indicating if access was granted (`true`) or denied (`false`).
 */
@Serializable
data class LoginResponse (
    val granted: Boolean,
)