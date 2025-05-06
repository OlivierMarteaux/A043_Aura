package com.aura.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aura.AuraApplication
import com.aura.data.model.Account
import com.aura.data.model.ServerConnection
import com.aura.data.repository.AuraRepository
import com.aura.data.repository.UserPreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val identifier: String = "",
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = false,
    val isError: String? = null,
    val balance: Double = 0.0,
)

/**
 * ViewModel for managing the state and logic of the Home screen.
 *
 * Handles retrieving account information, observing stored user input from preferences,
 * and computing the account balance. It exposes a [HomeUiState] via [uiState] to be observed
 * by the UI layer.
 *
 * @property auraRepository Repository used to interact with the backend server for account operations.
 * @property userPreferencesRepository Repository for accessing and observing user-stored preferences.
 */
class HomeViewModel(
    private val auraRepository: AuraRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Observe the stored user identifier and update the UI state accordingly.
        viewModelScope.launch {
            userPreferencesRepository.userInput.collect { storedId ->
                _uiState.update {
                    it.copy(
                        identifier = storedId,
                    )
                }
            }
        }
        // Automatically fetch accounts on initialization.
        getAccounts()
    }

    /**
     * Computes the total balance across all given accounts.
     *
     * @param accounts List of accounts.
     * @return The total balance as a [Double].
     */
    private fun getBalance(accounts: List<Account>): Double {
        return accounts.sumOf { it.balance }
    }

    /**
     * Retrieves the list of accounts associated with the current user identifier.
     *
     * Updates the UI state based on the result:
     * - Shows loading state while fetching.
     * - On success, populates the list and balance.
     * - On error, displays an error message.
     */
    fun getAccounts() {
        viewModelScope.launch {
            val identifier = uiState.value.identifier
            // reset uiState:
            _uiState.update {
                it.copy(
                    accounts = emptyList(),
                    isLoading = true,
                    isError = null,
                )
            }
            // get accounts from repository:
            when (val result = auraRepository.getAccounts(identifier)) {

                is ServerConnection.Success -> {
                    Log.d("HomeViewModel", "Success")
                    _uiState.update {
                        it.copy(
                            accounts = result.data,
                            isLoading = false,
                            isError = null,
                            balance = getBalance(result.data),
                        )
                    }
                }

                is ServerConnection.Loading -> {
                    Log.d("HomeViewModel", "Loading")
                    _uiState.update {
                        it.copy(
                            accounts = emptyList(),
                            isLoading = true,
                            isError = null,
                        )
                    }
                }

                is ServerConnection.Error -> {
                    Log.d("HomeViewModel", "Error: ${result.exception.message ?: "unknown error"}")
                    _uiState.update {
                        it.copy(
                            accounts = emptyList(),
                            isLoading = false,
                            isError = result.exception.message ?: "Unknown error",
                        )
                    }
                }
            }
        }
    }

    suspend fun resetUiState(){
        delay(500)
        _uiState.update { it.copy(
            accounts = emptyList(),
            isLoading = false,
            isError = null,
            balance = 0.0,
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AuraApplication)
                HomeViewModel(
                    auraRepository = application.container.auraRepository,
                    userPreferencesRepository = application.userPreferencesRepository,
                )
            }
        }
    }
}