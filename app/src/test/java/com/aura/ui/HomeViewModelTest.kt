package com.aura.ui

import android.util.Log
import com.aura.data.model.Account
import com.aura.data.model.ServerConnection
import com.aura.data.repository.AuraRepository
import com.aura.data.repository.UserPreferencesRepository
import com.aura.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var auraRepository: AuraRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var accounts: List<Account>

    @Before
    fun setup() {
        auraRepository = mockk()
        userPreferencesRepository = mockk()
        // Mock stored identifier (flowOf emits one value immediately)
        coEvery { userPreferencesRepository.userInput } returns flowOf("initialIdentifier")
        // Mock server response
        coEvery { auraRepository.getAccounts(any()) } returns ServerConnection.Loading
        // Mock Log.d calls
        mockkStatic(Log::class) // enables mocking static methods like Log.d
        every { Log.d(any(), any()) } returns 0
        // 🔧 redirects Dispatchers.Main
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(auraRepository, userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 🧹 resets after test
    }

    @Test
    fun homeViewModel_Initialization_ExpectedLoadingState() = runTest { // runTest:coroutine test
//        advanceUntilIdle() // Wait for the coroutine to complete
        with (viewModel.uiState.value) {
            // Assert user login fetched from UserPreferences Repository
            assertEquals("initialIdentifier",identifier)
            // Assert accounts empty
            assertTrue(accounts.isEmpty())
            // Assert loading state is enabled
            assertTrue(isLoading)
            // Assert Error state is null
            assertTrue(isError == null)
            // Assert initial balance is 0.0
            assertEquals(0.0, balance)
        }
    }

    @Test
    fun homeViewModel_OnSuccessfulServerConnection_ExpectedSuccessState() = runTest{
        // Mock successful server response
        val accounts = listOf(Account("1", true, 100.0), Account("2", false, 200.0))
        coEvery { auraRepository.getAccounts(any()) } returns ServerConnection.Success(accounts)
        viewModel.getAccounts()
        advanceUntilIdle() // Wait for the coroutine to complete

        with (viewModel.uiState.value) {
            // Assert user login correctly fetched from UserPreferences Repository
            assertEquals("initialIdentifier",identifier)
            // Assert accounts fetched from server
            assertEquals(accounts, this.accounts)
            // Assert loading state is disabled
            assertFalse(isLoading)
            // Assert error state is null
            assertTrue(isError == null)
            // Assert balance is correct
            assertEquals(300.0, balance)
        }
    }

    @Test
    fun homeViewModel_OnServerError_ExpectedErrorState() = runTest {
        // Mock server response
        val errorMessage = "Server error"
        coEvery { auraRepository.getAccounts(any()) } returns ServerConnection.Error(
            Exception(
                errorMessage
            )
        )
        viewModel.getAccounts()
        advanceUntilIdle() // Wait for the coroutine to complete
        with(viewModel.uiState.value) {
            // Assert user login correctly fetched from UserPreferences Repository
            assertEquals("initialIdentifier", identifier)
            // Assert accounts empty
            assertTrue(accounts.isEmpty())
            // Assert loading state is disabled
            assertFalse(isLoading)
            // Assert error state is not null
            assertEquals(errorMessage, isError)
            // Assert initial balance is 0.0
            assertEquals(0.0, balance)
        }
    }
}