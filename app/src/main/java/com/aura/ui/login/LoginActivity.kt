package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
  private val loginViewModel: LoginViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val login = binding.login
    val loading = binding.loading
    val identifier = binding.identifier
    val password = binding.password

    // Add text change listener to identifier
    identifier.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Optional: handle logic before the text changes
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Called when the text is being changed
        println("Identifier changed: $s")
      }

      override fun afterTextChanged(s: Editable?) {
        loginViewModel.getIdentifier(s.toString())
      }
    })

    // Add text change listener to password
    password.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        println("Password changed: $s")
      }
      override fun afterTextChanged(s: Editable?) {
        loginViewModel.getPassword(s.toString())
      }
    })


    login.setOnClickListener {
      loading.visibility = View.VISIBLE

      val intent = Intent(this@LoginActivity, HomeActivity::class.java)
      startActivity(intent)

      finish()
    }
  }
}
