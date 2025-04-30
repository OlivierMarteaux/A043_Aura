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

class HomeViewModel(
    private val auraRepository: AuraRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userInput.collect { storedId ->
                _uiState.update {
                    it.copy(
                        identifier = storedId,
                    )
                }
            }
        }
        getAccounts()
    }

    private fun getBalance(accounts: List<Account>): Double {
        return accounts.sumOf { it.balance }
    }

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