package com.aura.ui.login

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
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import kotlinx.coroutines.launch

/**
 * LoginActivity handles user login to the application.
 * It simulates a login process and redirects to the HomeActivity.
 *
 * This activity includes:
 * - A login Button to simulate login.
 * - A loading View that is displayed during the login process.
 *
 * This is a simplified example without actual authentication logic.
 * In a real application, validation and network calls would be implemented.
 *
 * Once the login is successful, the HomeActivity is launched and LoginActivity is finished.
 *
 */
class LoginActivity : AppCompatActivity()
{
  /**
   * The binding for the login layout.
   */
  private lateinit var binding: ActivityLoginBinding
  /**
   * The ViewModel for handling login logic.
   */
  private val loginViewModel: LoginViewModel by viewModels {LoginViewModel.Factory}

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Add text change listener to identifier
    binding.identifier.apply {
      doOnTextChanged { text, _, _, _ ->
      loginViewModel.getIdentifier(text.toString())
      loginViewModel.saveUserInput(text.toString())
    }
    }

    // Add text change listener to password
    binding.password.doOnTextChanged { text, _, _, _ ->
      loginViewModel.getPassword(text.toString())
    }

    // Handle login button click
    binding.login.setOnClickListener {
      binding.root.hideKeyboard()
      loginViewModel.onLoginClicked()
    }

    // Close keyboard on focus change
    binding.identifier.setOnFocusChangeListener { _, hasFocus ->
      if (!hasFocus) binding.root.hideKeyboard()
    }
    binding.password.setOnFocusChangeListener { _, hasFocus ->
      if (!hasFocus) binding.root.hideKeyboard()
    }

    // Collect UI state to update UI
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        loginViewModel.uiState.collect {
          if (binding.identifier.text.toString() != it.identifier) {
            binding.identifier.setText(it.identifier)
          }
          // Show Loading
          binding.loading.isVisible = it.isLoading
          // Enable Login
          binding.login.isEnabled = it.isEnabled
          // Show Error if any
          it.isError?.let{ toast(it) }
          // Navigate if granted
          it.isGranted?.let { when (it) { true -> {
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
          } else -> { toast("permission denied") }}}
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
    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    loginViewModel.resetUiState()
  }

  /**
   * Hides the soft keyboard if it's currently displayed.
   *
   * This extension function is used to dismiss the keyboard, often after user input,
   * ensuring a cleaner UI experience.
   */
  private fun View.hideKeyboard() {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
  }
}
