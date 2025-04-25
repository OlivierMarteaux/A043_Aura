package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aura.databinding.ActivityTransferBinding

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

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val recipient = binding.recipient
    val amount = binding.amount
    val transfer = binding.transfer
    val loading = binding.loading

    transfer.setOnClickListener {
      loading.visibility = View.VISIBLE

      setResult(Activity.RESULT_OK)
      finish()
    }
  }

}
