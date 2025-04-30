package com.aura.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.transfer.TransferActivity
import kotlinx.coroutines.launch

/**
 * HomeActivity is the main screen shown after a successful login.
 * It displays the user's account balance and allows the user to initiate a transfer.
 *
 * This activity includes:
 * - A TextView to show the current balance.
 * - A Button to start the transfer process.
 * - A menu option to log out and return to the login screen.
 *
 */
class HomeActivity : AppCompatActivity()
{

  /**
   * The binding for the home layout.
   */
  private lateinit var binding: ActivityHomeBinding

  private val homeViewModel: HomeViewModel by viewModels { HomeViewModel.Factory}

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      if (result.resultCode == Activity.RESULT_OK) {
        // The transfer succeeded — refresh the balance here
        homeViewModel.getAccounts()
      }
    }

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val balance = binding.balance
    val transfer = binding.transfer

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        homeViewModel.uiState.collect {
          // Show error if any
          it.isError?.let{ toast(it) }
          // Show Loading
          binding.loading.isVisible = it.isLoading
          // Get balance
          balance.text = it.balance.toString()
        }
      }
    }

    transfer.setOnClickListener {
      startTransferActivityForResult.launch(Intent(this@HomeActivity, TransferActivity::class.java))
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean
  {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return when (item.itemId)
    {
      R.id.disconnect ->
      {
//        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
//        finish()
        finishAffinity()
//        System.exit(0) // to force killing the app
        true
      }
      R.id.refresh ->
        {
          homeViewModel.getAccounts()
        true
      }
      else            -> super.onOptionsItemSelected(item)
    }
  }

  private suspend fun toast(message: String) {
    Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
    homeViewModel.resetUiState()
  }
}