package com.aura.ui

import com.aura.data.repository.AuraRepository
import com.aura.data.repository.UserPreferencesRepository
import com.aura.ui.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
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
        coEvery { userPreferencesRepository.userInput } returns flowOf("")

        Dispatchers.setMain(testDispatcher) // 🔧 redirects Dispatchers.Main
        viewModel = LoginViewModel(auraRepository, userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 🧹 resets after test
    }

    @Test
    fun loginViewModel_IdentifierAndPasswordNotEmpty_isLoginEnabled() = runTest {
        // Act
        viewModel.getIdentifier("testUser")
        viewModel.getPassword("testPassword")

        // Assert
        val currentState = viewModel.uiState.value
        assertTrue(currentState.isLoginEnabled)
    }
}