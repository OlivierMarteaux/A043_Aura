package com.aura.ui.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.home.HomeActivity
import kotlinx.coroutines.launch

/**
 * TransferActivity allows the user to enter transfer details and confirm the transaction.
 *
 * This activity includes:
 * - Input fields for the recipient and amount.
 * - A transfer Button to confirm the action.
 * - A loading View that is shown during the transfer.
 *
 * Once the transfer is confirmed, the activity returns a result to the calling activity
 * and finishes itself.
 *
 * Note: This is a simplified mockup and does not include real transaction handling.
 *
 * @see android.app.Activity.setResult
 * @see android.app.Activity.RESULT_OK
 * @see android.app.Activity.finish
 *
 */
class TransferActivity : AppCompatActivity()
{

  /**
   * The binding for the transfer layout.
   */
  private lateinit var binding: ActivityTransferBinding

  private val transferViewModel: TransferViewModel by viewModels{TransferViewModel.Factory}

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val recipient = binding.recipient
    val amount = binding.amount
    val transfer = binding.transfer
    val loading = binding.loading

    // Add text change listener to recipient
    binding.recipient.doOnTextChanged { text, _, _, _ ->
      transferViewModel.getRecipient(text.toString())
    }

    // Add text change listener to amount
    binding.amount.doOnTextChanged { text, _, _, _ ->
      transferViewModel.getAmount(text.toString())
    }

    transfer.setOnClickListener {
      binding.root.hideKeyboard()
      transferViewModel.onTransferClicked()
    }

    // Close keyboard on focus change
    binding.recipient.setOnFocusChangeListener { _, hasFocus ->
      if (!hasFocus) binding.root.hideKeyboard()
    }
    binding.amount.setOnFocusChangeListener { _, hasFocus ->
      if (!hasFocus) binding.root.hideKeyboard()
    }

    // Collect UI state to update UI
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        transferViewModel.uiState.collect {
          // Show Loading
          binding.loading.isVisible = it.isLoading
          // Enable Transfer
          binding.transfer.isEnabled = it.isTransferEnabled
          // Show Error if any
          it.isError?.let{ toast(it) }
          // Navigate if granted
          it.isGranted?.let { when (it) { true -> {
            setResult(Activity.RESULT_OK)
            finish()
          } else -> { toast("transfer failed") }}}
        }
      }
    }
  }

  /**
   * Displays a short Toast message to the user and resets the UI state in the ViewModel.
   *
   * This method provides quick feedback to the user (e.g., after an action or error)
   * and clears any transient UI state after the message is shown.
   *
   * @param message The message to be shown in the Toast.
   */
  private suspend fun toast(message: String) {
    Toast.makeText(this@TransferActivity, message, Toast.LENGTH_SHORT).show()
    transferViewModel.resetUiState()
  }

  /**
   * Hides the soft keyboard if it's currently displayed.
   *
   * This extension function is used to dismiss the keyboard, often after user input,
   * ensuring a cleaner UI experience.
   */
  private fun View.hideKeyboard() {
    val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
  }
}