package com.aura.ui

import com.aura.data.model.ServerConnection
import com.aura.data.repository.AuraRepository
import com.aura.data.repository.UserPreferencesRepository
import com.aura.ui.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private lateinit var auraRepository: AuraRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        auraRepository = mockk()
        userPreferencesRepository = mockk()

        // Mock stored identifier (flowOf emits one value immediately)
        coEvery { userPreferencesRepository.userInput } returns flowOf("initialIdentifier")

        Dispatchers.setMain(testDispatcher) // 🔧 redirects Dispatchers.Main
        viewModel = LoginViewModel(auraRepository, userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 🧹 resets after test
    }

    @Test
    fun loginViewModel_Initialization_ExpectedInitialState() = runTest { // runTest:coroutine test

        // user login fetched from UserPreferences Repository
        assertEquals(viewModel.uiState.value.identifier, "initialIdentifier")
        // password field empty
        assertEquals(viewModel.uiState.value.password, "")
        // login button disabled
        assertFalse(viewModel.uiState.value.isEnabled)
        // loading state disabled
        assertFalse(viewModel.uiState.value.isLoading)
        // Error state is null
        assertEquals(viewModel.uiState.value.isError, null)
        // Granted state is null
        assertEquals(viewModel.uiState.value.isGranted, null)
    }

    @Test
    fun loginViewModel_IdentifierAndPasswordNotEmpty_LoginButtonEnabled() = runTest {
        // When login and password are not empty
        viewModel.getIdentifier("testUser")
        viewModel.getPassword("testPassword")

        // Assert login button is enabled
        val currentState = viewModel.uiState.value
        assertTrue(currentState.isEnabled)
    }

    @Test
    fun loginViewModel_IdentifierEmpty_LoginButtonDisabled() = runTest {
        //When login is empty
        viewModel.getIdentifier("")
        viewModel.getPassword("testPassword")

        // Assert login button is disabled
        val currentState = viewModel.uiState.value
        assertFalse(currentState.isEnabled)
    }

    @Test
    fun loginViewModel_PasswordEmpty_LoginButtonDisabled() = runTest {
        // When password is empty
        viewModel.getIdentifier("testUser")
        viewModel.getPassword("")

        // Assert login button is disabled
        val currentState = viewModel.uiState.value
        assertFalse(currentState.isEnabled)
    }

    @Test
    fun loginViewModel_OnLoginButtonClick_ServerConnectionIsLoadingAsExpected() = runTest {
        // Mock server response
        coEvery { auraRepository.login(any(), any()) } returns ServerConnection.Loading

        // When login button is clicked
        viewModel.onLoginClicked()
        advanceUntilIdle() // Wait for the coroutine to complete
        val currentState = viewModel.uiState.value

        // Assert loading state is enabled
        assertTrue(currentState.isLoading)
        // Assert error state is null
        assertEquals(currentState.isError, null)
        // Assert granted state is null
        assertEquals(currentState.isGranted, null)
        // Assert login button is disabled
        assertFalse(currentState.isEnabled)
    }

    @Test
    fun loginViewModel_OnSuccessfulServerConnectionAndUnauthorizedId_AccessDenied() = runTest {
        // Mock server response
        coEvery { auraRepository.login(any(), any()) } returns ServerConnection.Success(false)
        // When login button is clicked
        viewModel.onLoginClicked()
        advanceUntilIdle() // Wait for the coroutine to complete
        val currentState = viewModel.uiState.value
        // Assert loading state is disabled
        assertFalse(currentState.isLoading)
        // Assert error state is null
        assertEquals(currentState.isError, null)
        // Assert granted state is false
        assertEquals(currentState.isGranted, false)
        // Assert login button is enabled
        assertTrue(currentState.isEnabled)
    }

    @Test
    fun loginViewModel_OnSuccessfulServerConnectionAndAuthorizedId_AccessGranted() = runTest {
        // Mock server response
        coEvery { auraRepository.login(any(), any()) } returns ServerConnection.Success(true)
        // When login button is clicked
        viewModel.onLoginClicked()
        advanceUntilIdle() // Wait for the coroutine to complete
        val currentState = viewModel.uiState.value
        // Assert loading state is disabled
        assertFalse(currentState.isLoading)
        // Assert error state is null
        assertEquals(currentState.isError, null)
        // Assert granted state is true
        assertEquals(currentState.isGranted, true)
        // Assert login button is enabled
        assertTrue(currentState.isEnabled)
    }

    @Test
    fun loginViewModel_OnServerError_ErrorStateAsExpected() = runTest {
        // Mock server response
        val errorMessage = "Server error"
        coEvery { auraRepository.login(any(), any()) } returns ServerConnection.Error(Exception(errorMessage))

        // When login button is clicked
        viewModel.onLoginClicked()
        advanceUntilIdle() // Wait for the coroutine to complete
        val currentState = viewModel.uiState.value

        // Assert loading state is disabled
        assertFalse(currentState.isLoading)
        // Assert error state is not null
        assertEquals(currentState.isError, errorMessage)
        // Assert granted state is null
        assertEquals(currentState.isGranted, null)
        // Assert login button is enabled
        assertTrue(currentState.isEnabled)
    }
}