package com.aura.ui

import com.aura.data.repository.AuraRepository
import com.aura.data.repository.UserPreferencesRepository
import com.aura.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel
    private lateinit var auraRepository: AuraRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        auraRepository = mockk()
        userPreferencesRepository = mockk()
        // Mock stored identifier (flowOf emits one value immediately)
        coEvery { userPreferencesRepository.userInput } returns flowOf("initialIdentifier")
        // 🔧 redirects Dispatchers.Main
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(auraRepository, userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 🧹 resets after test
    }

    @Test
    fun homeViewModel_Initialization_ExpectedInitialState() {
        with (viewModel.uiState.value) {

        // user login fetched from UserPreferences Repository
        assert(viewModel.uiState.value.identifier == "initialIdentifier")
        // accounts empty
        assert(viewModel.uiState.value.accounts.isEmpty())
        // loading state
//        assertTrue(viewModel.uiState.value.isLoading)
        // Error state is null
        assert(viewModel.uiState.value.isError == null)
        // Balance is 0.0
        assert(viewModel.uiState.value.balance == 0.0)
        }
    }


}