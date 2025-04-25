package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    binding.identifier.doOnTextChanged { text, _, _, _ ->
      loginViewModel.getIdentifier(text.toString())
    }

    // Add text change listener to password
    binding.password.doOnTextChanged { text, _, _, _ ->
      loginViewModel.getPassword(text.toString())
    }

    // Collect UI state to update UI
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        loginViewModel.uiState.collect { state ->
          binding.login.isEnabled = state.isLoggable
          if (state.isLoading) {
            binding.loading.visibility = View.VISIBLE
            binding.login.isEnabled = false
          } else {
            binding.loading.visibility = View.GONE
            if (state.isGranted == true){
              startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
              finish()
            } else if (state.isGranted == false) {
              Toast.makeText(this@LoginActivity, "permission denied", Toast.LENGTH_LONG).show()
            }
          }
        }
      }
    }

    // Handle login button click
    binding.login.setOnClickListener {
      loginViewModel.onLoginClicked()
//      if (loginViewModel.uiState.value.isGranted){
//      startActivity(Intent(this, HomeActivity::class.java))
//      finish()
//      } else {
//        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
//      }
    }
  }
}
