package com.aura.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val identifier: String,
    val password: String
)

class LoginViewModel: ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(LoginUiState("", ""))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun getLogin(username: String, password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                identifier = username,
                password = password
            )
        }
    }

    fun getIdentifier(identifier:String){
        _uiState.update { currentState ->
            currentState.copy(
                identifier = identifier
            )
        }
    }

    fun getPassword(password:String){
        _uiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }



}