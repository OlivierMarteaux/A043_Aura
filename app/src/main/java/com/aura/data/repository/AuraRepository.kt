package com.aura.data.repository

import android.util.Log
import com.aura.data.model.Account
import com.aura.data.model.LoginRequest
import com.aura.data.model.ServerConnection
import com.aura.data.model.Transfer
import com.aura.data.network.AuraClient
import kotlinx.coroutines.delay

/**
 * Repository interface responsible for handling user authentication logic.
 *
 * This abstraction allows different implementations of login behavior (e.g., network, mock, or local database),
 * promoting testability and separation of concerns.
 */
interface AuraRepository{
    /**
     * Attempts to log in the user using the provided credentials.
     *
     * @param id The user's identifier (e.g., username or email).
     * @param password The user's password.
     * @return `true` if login is successful (access granted), `false` otherwise.
     * @throws Exception if the network request fails or the server returns an error.
     */
    suspend fun login(id: String, password: String): ServerConnection<Boolean>
    suspend fun getAccounts(id: String): ServerConnection<List<Account>>
    suspend fun doTransfer(sender: String, recipient: String, amount: Double): ServerConnection<Boolean>
}

/**
 * Network-based implementation of [AuraRepository] that uses [AuraClient] to perform the login.
 *
 * This implementation sends the login credentials to a remote API and returns the result
 * based on the server's response.
 *
 * @param auraClient The Retrofit service interface used to make network requests.
 */
class NetworkAuraRepository(private val auraClient: AuraClient): AuraRepository {

    override suspend fun login(id: String, password: String): ServerConnection<Boolean> {
        return try {
            ServerConnection.Loading
            val loginRequest = LoginRequest(id, password)
            val loginResponse = auraClient.login(loginRequest)
            ServerConnection.Success(loginResponse.granted)
        } catch (e: Exception) {
            ServerConnection.Error(e)
        }
    }
    override suspend fun getAccounts(id: String): ServerConnection<List<Account>> {
        return try {
            ServerConnection.Loading
            val accounts = auraClient.getAccounts(id)
            ServerConnection.Success(accounts)
        } catch (e: Exception) {
            ServerConnection.Error(e)
        }
    }
    override suspend fun doTransfer(sender: String, recipient: String, amount: Double): ServerConnection<Boolean> {
        return try {
            ServerConnection.Loading
            Log.d("OM_TAG", "RepositorydoTransfer: $sender $recipient $amount")
            val transfer = Transfer(sender, recipient, amount)
            val transferResult = auraClient.doTransfer(transfer)
            ServerConnection.Success(transferResult.result)
        } catch (e: Exception) {
            ServerConnection.Error(e)
        }
    }
}