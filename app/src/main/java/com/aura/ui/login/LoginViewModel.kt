package com.aura.ui.login

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val username: String,
    val password: String
)

class LoginViewModel {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(LoginUiState("", ""))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun getLogin(username: String, password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                username = username,
                password = password
            )
        }
    }



}