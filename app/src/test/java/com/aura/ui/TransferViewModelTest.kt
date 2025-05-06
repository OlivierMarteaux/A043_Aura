package com.aura.ui

import android.util.Log
import com.aura.data.model.ServerConnection
import com.aura.data.repository.AuraRepository
import com.aura.data.repository.UserPreferencesRepository
import com.aura.ui.transfer.TransferViewModel
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
class TransferViewModelTest {
    private lateinit var viewModel: TransferViewModel
    private lateinit var auraRepository: AuraRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        auraRepository = mockk()
        userPreferencesRepository = mockk()
        // Mock stored identifier (flowOf emits one value immediately)
        coEvery { userPreferencesRepository.userInput } returns flowOf("initialIdentifier")
        // Mock Log.d calls
        mockkStatic(Log::class) // enables mocking static methods like Log.d
        every { Log.d(any(), any()) } returns 0
        // 🔧 redirects Dispatchers.Main
        Dispatchers.setMain(testDispatcher)
        viewModel = TransferViewModel(auraRepository, userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 🧹 resets after test
    }

    @Test
    fun transferViewModel_Initialization_ExpectedInitialState() = runTest { // runTest:coroutine test
        with (viewModel.uiState.value) {
            // Assert user login fetched from UserPreferences Repository
            assertEquals("initialIdentifier",sender)
            // Assert recipient is empty
            assertTrue(recipient.isEmpty())
            // Assert amount is empty
            assertTrue(amount.isEmpty())
            // Assert transfer is disabled
            assertFalse(isTransferEnabled)
            // Assert loading state is disabled
            assertFalse(isLoading)
            // Assert error state is null
            assertTrue(isError == null)
            // Assert granted state is null
            assertTrue(isGranted == null)
        }
    }

    @Test
    fun transferViewModel_RecipientAndAmountNotEmpty_TransferButtonEnabled() =runTest{
        // When recipient and amount are not empty
        viewModel.getRecipient("testRecipient")
        viewModel.getAmount("100.0")

        // Assert transfer button is enabled
        val currentState = viewModel.uiState.value
        assertTrue(currentState.isTransferEnabled)
    }

    @Test
    fun transferViewModel_RecipientEmpty_TransferButtonDisabled() = runTest{
        // When recipient is empty
        viewModel.getRecipient("")
        viewModel.getAmount("100.0")

        // Assert transfer button is disabled
        val currentState = viewModel.uiState.value
        assertFalse(currentState.isTransferEnabled)
    }

    @Test
    fun transferViewModel_AmountEmpty_TransferButtonDisabled() = runTest{
        // When amount is empty
        viewModel.getRecipient("testRecipient")
        viewModel.getAmount("")

        // Assert transfer button is disabled
        val currentState = viewModel.uiState.value
        assertFalse(currentState.isTransferEnabled)
    }

    @Test
    fun transferViewModel_OnTransferButtonClick_ServerConnectionIsLoadingAsExpected() = runTest {
        // Mock server response
        coEvery { auraRepository.doTransfer(any(), any(), any()) } returns ServerConnection.Loading
        // Given correct recipient and amount
        viewModel.getRecipient("testRecipient")
        viewModel.getAmount("200")
        // When transfer button is clicked
        viewModel.onTransferClicked()
        advanceUntilIdle() // Wait for the coroutine to complete
        val currentState = viewModel.uiState.value

        // Assert loading state is enabled
        assertTrue(currentState.isLoading)
        // Assert error state is null
        assertTrue(currentState.isError == null)
        // Assert granted state is null
        assertTrue(currentState.isGranted == null)
        // Assert transfer button is disabled
        assertFalse(currentState.isTransferEnabled)
    }

    @Test
    fun transferViewModel_OnSuccessfulServerConnectionAndUnauthorizedId_AccessDeniedAsExpected() = runTest {
        // Mock server response
        coEvery { auraRepository.doTransfer(any(), any(), any()) } returns ServerConnection.Success(false)
        // Given correct recipient and amount
        viewModel.getRecipient("testRecipient")
        viewModel.getAmount("200")
        // When transfer button is clicked
        viewModel.getAmount("200")
        viewModel.onTransferClicked()
        advanceUntilIdle() // Wait for the coroutine to complete
        val currentState = viewModel.uiState.value

        // Assert loading state is disabled
        assertFalse(currentState.isLoading)
        // Assert error state is null
        assertTrue(currentState.isError == null)
        // Assert granted state is false
        assertTrue(currentState.isGranted == false)
        // Assert transfer button is enabled
        assertTrue(currentState.isTransferEnabled)
    }

    @Test
    fun transferViewModel_OnServerError_ErrorStateAsExpected() = runTest{
        // Mock server response
        val errorMessage = "Server error"
        coEvery { auraRepository.doTransfer(any(), any(), any()) } returns ServerConnection.Error(Exception(errorMessage))
        // Given correct recipient and amount
        viewModel.getRecipient("testRecipient")
        viewModel.getAmount("200")
        // When transfer button is clicked
        viewModel.onTransferClicked()
        advanceUntilIdle() // Wait for the coroutine to complete
        val currentState = viewModel.uiState.value

        // Assert loading state is disabled
        assertFalse(currentState.isLoading)
        // Assert error state is not null
        assertEquals(errorMessage, currentState.isError)
        // Assert granted state is null
        assertTrue(currentState.isGranted == null)
        // Assert transfer button is enabled
        assertTrue(currentState.isTransferEnabled)
    }

    @Test
    fun transferViewModel_OnSuccessfulServerConnectionAndAuthorizedId_AccessGrantedAsExpected() = runTest {
        // Mock server response
        coEvery { auraRepository.doTransfer(any(), any(), any()) } returns ServerConnection.Success(true)

        // Given correct recipient and amount
        viewModel.getRecipient("testRecipient")
        viewModel.getAmount("200")

        // When transfer button is clicked
        viewModel.onTransferClicked()
        advanceUntilIdle() // Wait for the coroutine to complete
        val currentState = viewModel.uiState.value

        // Assert loading state is disabled
        assertFalse(currentState.isLoading)
        // Assert error state is null
        assertTrue(currentState.isError == null)
        // Assert granted state is true
        assertTrue(currentState.isGranted == true)
        // Assert transfer button is enabled
        assertTrue(currentState.isTransferEnabled)
    }
}