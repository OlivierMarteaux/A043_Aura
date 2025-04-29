package com.aura.ui.home

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
import com.aura.ui.login.LoginUiState
import com.aura.ui.login.LoginViewModel
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
                _uiState.update { it.copy(
                    identifier = storedId,
                    )
                }
            }
        }
    }

    private fun getAccounts(identifier: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(
                accounts = emptyList(),
                isLoading = true,
                isError = null,
            )
            }
            val result = auraRepository.getAccounts(identifier)
            when (result) {
                is ServerConnection.Success ->
                    _uiState.update { it.copy(
                        accounts = result.data,
                        isLoading = false,
                        isError = null,
                    )
                }

                is ServerConnection.Loading -> {
                    _uiState.update { it.copy(
                        accounts = emptyList(),
                        isLoading = true,
                        isError = null,
                        )
                    }
                }

                is ServerConnection.Error -> {
                    _uiState.update { it.copy(
                        accounts = emptyList(),
                        isLoading = false,
                        isError = result.exception.message ?: "Unknown error",
                    )
                    }
                }
            }
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