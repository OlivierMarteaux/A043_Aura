package com.aura.ui.login

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

/**
 * UI state model for the login screen.
 *
 * Represents all relevant data needed to render and interact with the login UI.
 *
 * @property identifier The user's input for the login identifier (e.g., username or email).
 * @property password The user's input for the login password.
 * @property isEnabled A flag indicating whether the login button should be enabled.
 *                      Typically true when both [identifier] and [password] are not empty.
 * @property isLoading A flag indicating whether a login operation is in progress,
 *                     used to show or hide a loading indicator.
 */
data class LoginUiState(
    val identifier: String,
    val password: String,
    val isEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isGranted: Boolean? = null,
    val isError: String? = null,
)

/**
 * ViewModel responsible for managing the state of the login screen.
 *
 * It holds the user input for identifier and password,
 * and computes whether the login action can be triggered (`isLoggable`).
 *
 * State is exposed as an immutable [StateFlow] to be observed by the UI.
 */
class LoginViewModel(
    private val auraRepository: AuraRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(LoginUiState("", "", false))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

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

    /**
     * Updates the identifier field in the UI state.
     *
     * Also triggers a recalculation of [isLoginEnabled] to determine
     * whether the login button should be enabled.
     *
     * @param identifier The new identifier value entered by the user.
     */
    fun getIdentifier(identifier:String){
        _uiState.update { it.copy(
            identifier = identifier,
            isEnabled = isLoginEnabled(identifier, it.password),
            )
        }
    }

    /**
     * Updates the password field in the UI state.
     *
     * Also triggers a recalculation of [isLoginEnabled] to determine
     * whether the login button should be enabled.
     *
     * @param password The new password value entered by the user.
     */
    fun getPassword(password:String){
        _uiState.update { it.copy(
            password = password,
            isEnabled = isLoginEnabled(it.identifier, password),
            )
        }
    }

    fun saveUserInput(identifier: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveUserInput(identifier)
        }
    }

    /**
     * Evaluates whether the login action is allowed based on the current UI state.
     *
     * @return True if both identifier and password are not empty.
     */
    private fun isLoginEnabled(identifier: String, password: String) : Boolean {
        return identifier.isNotEmpty() && password.isNotEmpty()
    }

    /**
     * Handles the login button click event.
     *
     * This method launches a coroutine in the [viewModelScope] to perform an asynchronous login operation.
     * It first sets the UI state to loading, then attempts to log in using the current credentials
     * stored in [uiState], and finally updates the UI state with the result.
     *
     * The method updates:
     * - `isLoading` to `true` before the login request starts.
     * - `isGranted` with the result of the login attempt.
     * - `isLoading` back to `false` once the request completes.
     *
     * This function does not directly navigate or display messages—those should be handled by observing [uiState] from the UI layer.
     */
    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isGranted = null,
                isLoading = true,
                isError = null,
                isEnabled = false
                )
            }
            val serverConnection =
                with(uiState.value) { auraRepository.login(identifier, password) }

            when (serverConnection) {
                is ServerConnection.Success -> {
                    _uiState.update { it.copy(
                        isGranted = serverConnection.data,
                        isLoading = false,
                        isError = null,
                        isEnabled = true
                        )
                    }
                }

                is ServerConnection.Error -> {
                    _uiState.update { it.copy(
                        isGranted = null,
                        isLoading = false,
                        isError = serverConnection.exception.message ?: "Unknown error",
                        isEnabled = true
                        )
                    }
                }

                is ServerConnection.Loading -> {
                    _uiState.update { it.copy(
                        isGranted = null,
                        isLoading = true,
                        isError = null,
                        isEnabled = false,
                        )
                    }
                }
            }
        }
    }

    suspend fun resetUiState(){
        delay(200)
        _uiState.update { it.copy(
            isGranted = null,
            isLoading = false,
            isError = null,
            isEnabled = true
            )
        }
    }

    /**
     * Factory for creating an instance of [LoginViewModel] with application-level dependencies.
     *
     * This factory uses the [viewModelFactory] initializer DSL from the Jetpack `lifecycle-viewmodel-ktx` library
     * to access the [Application] context and retrieve the required dependencies (in this case, the [AuraRepository]).
     *
     * It casts the application instance to [AuraApplication], which exposes an [AppContainer] holding the repository.
     * This pattern ensures that the ViewModel can be created with the necessary dependencies without requiring
     * a dependency injection framework.
     *
     * Usage (e.g., in an Activity or Fragment):
     * ```
     * val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }
     * ```
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AuraApplication)
                LoginViewModel(
                    auraRepository = application.container.auraRepository,
                    userPreferencesRepository = application.userPreferencesRepository,
                    )
            }
        }
    }
}