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

    /**
     * Attempts to log in a user with the provided credentials.
     *
     * Simulates a delay for development purposes, sends a login request,
     * and returns the result wrapped in a [ServerConnection].
     *
     * @param id The user's identifier (e.g., username or email).
     * @param password The user's password.
     * @return A [ServerConnection] indicating the login result:
     * - [ServerConnection.Success] with `true` if access is granted.
     * - [ServerConnection.Error] if an exception occurs.
     */
    override suspend fun login(id: String, password: String): ServerConnection<Boolean> {
        return try {
            ServerConnection.Loading
            delay(2000) // for dev purpose
            val loginRequest = LoginRequest(id, password)
            val loginResponse = auraClient.login(loginRequest)
            ServerConnection.Success(loginResponse.granted)
        } catch (e: Exception) {
            ServerConnection.Error(e)
        }
    }

    /**
     * Retrieves a list of accounts associated with the given user ID.
     *
     * Simulates a delay for development purposes and fetches account data
     * from the remote source.
     *
     * @param id The user's identifier.
     * @return A [ServerConnection] containing a list of [Account] objects if successful,
     * or an [ServerConnection.Error] if an exception occurs.
     */
    override suspend fun getAccounts(id: String): ServerConnection<List<Account>> {
        return try {
            ServerConnection.Loading
            delay(2000) // for dev purpose
            val accounts = auraClient.getAccounts(id)
            ServerConnection.Success(accounts)
        } catch (e: Exception) {
            ServerConnection.Error(e)
        }
    }

    /**
     * Initiates a monetary transfer from one account to another.
     *
     * Simulates a delay for development purposes and sends a [Transfer] request
     * to the remote service.
     *
     * @param sender The ID of the account sending funds.
     * @param recipient The ID of the account receiving funds.
     * @param amount The amount of money to transfer.
     * @return A [ServerConnection] indicating success (`true`) or failure.
     */
    override suspend fun doTransfer(sender: String, recipient: String, amount: Double): ServerConnection<Boolean> {
        return try {
            ServerConnection.Loading
            delay(2000) // for dev purpose
            Log.d("OM_TAG", "RepositorydoTransfer: $sender $recipient $amount")
            val transfer = Transfer(sender, recipient, amount)
            val transferResult = auraClient.doTransfer(transfer)
            ServerConnection.Success(transferResult.result)
        } catch (e: Exception) {
            ServerConnection.Error(e)
        }
    }
}