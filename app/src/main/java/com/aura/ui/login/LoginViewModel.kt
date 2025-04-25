package com.aura.ui.login

import android.util.Log.isLoggable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state model for the login screen.
 *
 * Represents all relevant data needed to render and interact with the login UI.
 *
 * @property identifier The user's input for the login identifier (e.g., username or email).
 * @property password The user's input for the login password.
 * @property isLoggable A flag indicating whether the login button should be enabled.
 *                      Typically true when both [identifier] and [password] are not empty.
 * @property isLoading A flag indicating whether a login operation is in progress,
 *                     used to show or hide a loading indicator.
 */
data class LoginUiState(
    val identifier: String,
    val password: String,
    val isLoggable: Boolean,
    val isLoading: Boolean = false
)

/**
 * ViewModel responsible for managing the state of the login screen.
 *
 * It holds the user input for identifier and password,
 * and computes whether the login action can be triggered (`isLoggable`).
 *
 * State is exposed as an immutable [StateFlow] to be observed by the UI.
 */
class LoginViewModel: ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(LoginUiState("", "", false))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Updates the identifier field in the UI state.
     *
     * Also triggers a recalculation of [isLoggable] to determine
     * whether the login button should be enabled.
     *
     * @param identifier The new identifier value entered by the user.
     */
    fun getIdentifier(identifier:String){
        _uiState.update { currentState ->
            currentState.copy(
                identifier = identifier,
                isLoggable = isLoggable()
            )
        }
    }

    /**
     * Updates the password field in the UI state.
     *
     * Also triggers a recalculation of [isLoggable] to determine
     * whether the login button should be enabled.
     *
     * @param password The new password value entered by the user.
     */
    fun getPassword(password:String){
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                isLoggable = isLoggable()
            )
        }
    }

    /**
     * Evaluates whether the login action is allowed based on the current UI state.
     *
     * @return True if both identifier and password are not empty.
     */
    private fun isLoggable() = with(uiState.value) {
        identifier.isNotEmpty() && password.isNotEmpty()
    }

    /**
     * Triggers the loading state when the login action is initiated.
     *
     * This could be extended to include authentication logic and error handling.
     */
    fun onLoginClicked(){
        _uiState.update{state ->
            state.copy(isLoading = true)
        }
    }

}