package com.aura.ui.transfer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aura.AuraApplication
import com.aura.data.model.ServerConnection
import com.aura.data.repository.AuraRepository
import com.aura.data.repository.UserPreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransferUiState(
    val sender: String = "",
    val recipient: String = "",
    val isTransferEnabled: Boolean = false,
    val amount: String = "",
    val isGranted: Boolean? = null,
    val isLoading: Boolean = false,
    val isError: String? = null,
)

/**
 * ViewModel for managing the state and logic of the transfer screen.
 *
 * Handles updating the UI state for transfer operations, including setting the sender,
 * recipient, and amount. It manages the transfer process and updates the UI state
 * based on success, error, or loading states. It also observes the user's preferences
 * (sender) from the [UserPreferencesRepository].
 *
 * @property auraRepository Repository used to interact with the backend for transfer operations.
 * @property userPreferencesRepository Repository for accessing and observing user-stored preferences (e.g., sender).
 */
class TransferViewModel (
    private val auraRepository: AuraRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userInput.collect { storedId ->
                _uiState.update { it.copy(
                    sender = storedId,
                )
                }
            }
        }
    }

    /**
     * Updates the recipient of the transfer and enables/disables the transfer button
     * based on whether both recipient and amount are provided.
     *
     * @param recipient The recipient's identifier or account number.
     */
    fun getRecipient(recipient:String){
        _uiState.update { it.copy(
            recipient = recipient,
            isTransferEnabled = isTransferEnabled(recipient, it.amount),
            )
        }
    }

    /**
     * Updates the amount for the transfer and enables/disables the transfer button
     * based on whether both recipient and amount are provided.
     *
     * @param amount The amount of money to be transferred as a string.
     */
    fun getAmount(amount:String){
        _uiState.update { it.copy(
            amount = amount,
            isTransferEnabled = isTransferEnabled(it.recipient, amount),
            )
        }
    }

    /**
     * Checks if the transfer is enabled based on the provided recipient and amount.
     *
     * @param recipient The recipient's identifier.
     * @param amount The amount to be transferred.
     * @return True if both recipient and amount are provided, false otherwise.
     */
    private fun isTransferEnabled(recipient: String, amount: String) : Boolean {
        return recipient.isNotEmpty() && amount.isNotEmpty()
    }

    /**
     * Handles the transfer operation when the transfer button is clicked.
     * Updates the UI state to reflect the loading, success, or error states.
     * Initiates the transfer by calling the [auraRepository.doTransfer] method.
     */
    fun onTransferClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isTransferEnabled = false,
                isLoading = true,
                isError = null,
                isGranted = null,
                )
            }
            val serverConnection =
                with(uiState.value) { auraRepository.doTransfer(sender, recipient, amount.toDouble()) }

            when (serverConnection) {
                is ServerConnection.Success -> {
                    Log.d("OM_TAG", "Transfer connection success: transfer result = ${serverConnection.data}")
                    _uiState.update { it.copy(
                        isTransferEnabled = isTransferEnabled(it.recipient, it.amount),
                        isGranted = serverConnection.data,
                        isLoading = false,
                        isError = null,
                        )
                    }
                }

                is ServerConnection.Error -> {
                    Log.d("OM_TAG", "Transfer connection error: ${serverConnection.exception.message}")
                    _uiState.update { it.copy(
                        isTransferEnabled = isTransferEnabled(it.recipient, it.amount),
                        isGranted = null,
                        isLoading = false,
                        isError = serverConnection.exception.message ?: "Unknown error",
                    )
                    }
                }

                is ServerConnection.Loading -> {
                    _uiState.update { it.copy(
                        isTransferEnabled = false,
                        isLoading = true,
                        isError = null,
                        isGranted = null,
                        )
                    }
                }
            }
        }
    }

    /**
     * Resets the UI state to its initial state after a delay.
     * This is typically used to clear any results or errors after a transfer operation.
     */
    suspend fun resetUiState(){
        delay(500)
        _uiState.update { it.copy(
            isGranted = null,
            isLoading = false,
            isError = null,
        )
        }
    }

    /**
     * ViewModel factory for creating instances of [TransferViewModel].
     * Used for dependency injection with the application context.
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AuraApplication)
                TransferViewModel(
                    auraRepository = application.container.auraRepository,
                    userPreferencesRepository = application.userPreferencesRepository,
                )
            }
        }
    }
}