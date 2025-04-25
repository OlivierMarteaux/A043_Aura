package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity

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

  private val loginViewModel: LoginViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val login = binding.login
    val loading = binding.loading

    login.setOnClickListener {
      loading.visibility = View.VISIBLE

      val intent = Intent(this@LoginActivity, HomeActivity::class.java)
      startActivity(intent)

      finish()
    }
  }

  override fun onViewCreated(savedInstanceState: Bundle?) {
    super.onViewCreated(savedInstanceState)

  }

}
