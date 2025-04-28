package com.aura.data.repository

import com.aura.data.model.LoginRequest
import com.aura.data.model.ServerConnection
import com.aura.data.network.AuraClient

/**
 * Repository interface responsible for handling user authentication logic.
 *
 * This abstraction allows different implementations of login behavior (e.g., network, mock, or local database),
 * promoting testability and separation of concerns.
 */
interface LoginRepository{
    /**
     * Attempts to log in the user using the provided credentials.
     *
     * @param id The user's identifier (e.g., username or email).
     * @param password The user's password.
     * @return `true` if login is successful (access granted), `false` otherwise.
     * @throws Exception if the network request fails or the server returns an error.
     */
    suspend fun login(id: String, password: String): ServerConnection<Boolean>
}

/**
 * Network-based implementation of [LoginRepository] that uses [AuraClient] to perform the login.
 *
 * This implementation sends the login credentials to a remote API and returns the result
 * based on the server's response.
 *
 * @param auraClient The Retrofit service interface used to make network requests.
 */
class NetworkLoginRepository(private val auraClient: AuraClient): LoginRepository {
    override suspend fun login(id: String, password: String): ServerConnection<Boolean> {
        return try {
            val loginRequest = LoginRequest(id, password)
            val loginResponse = auraClient.login(loginRequest)
            ServerConnection.Success(loginResponse.granted)
        } catch (e: Exception) {
            ServerConnection.Error(e)
        }
    }
}