package com.aura.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing the payload sent to the login API.
 *
 * This class is serialized into JSON using [kotlinx.serialization] and contains
 * the user credentials required to authenticate.
 *
 * @property id The unique identifier for the user (e.g., username or email).
 * @property password The user's password.
 */
@Serializable
data class LoginRequest(
    val id: String,
    val password: String,
)