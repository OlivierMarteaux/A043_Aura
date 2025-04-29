package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity

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

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      //TODO
    }

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val balance = binding.balance
    val transfer = binding.transfer

    balance.text = "2654,54€"

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
        true
      }
      else            -> super.onOptionsItemSelected(item)
    }
  }

}
