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

    fun getRecipient(recipient:String){
        _uiState.update { it.copy(
            recipient = recipient,
            isTransferEnabled = isTransferEnabled(recipient, it.amount),
            )
        }
    }

    fun getAmount(amount:String){
        _uiState.update { it.copy(
            amount = amount,
            isTransferEnabled = isTransferEnabled(it.recipient, amount),
            )
        }
    }

    private fun isTransferEnabled(recipient: String, amount: String) : Boolean {
        return recipient.isNotEmpty() && amount.isNotEmpty()
    }

    fun onTransferClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isTransferEnabled = false,
                isLoading = true,
                )
            }
            val serverConnection =
                with(uiState.value) { auraRepository.doTransfer(sender, recipient, amount.toDouble()) }

            when (serverConnection) {
                is ServerConnection.Success -> {
                    Log.d("OM_TAG", "Transfer connection success: transfer result = ${serverConnection.data}")
                    _uiState.update { it.copy(
                        isGranted = serverConnection.data,
                        isLoading = false,
                        isError = null,
                    )
                    }
                }

                is ServerConnection.Error -> {
                    Log.d("OM_TAG", "Transfer connection error: ${serverConnection.exception.message}")
                    _uiState.update { it.copy(
                        isGranted = null,
                        isLoading = false,
                        isError = serverConnection.exception.message ?: "Unknown error",
                    )
                    }
                }

                is ServerConnection.Loading -> { _uiState.update { it.copy(isLoading = true) } }
            }
        }
    }

    suspend fun resetUiState(){
        delay(500)
        _uiState.update { it.copy(
            isGranted = null,
            isLoading = false,
            isError = null,
        )
        }
    }

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