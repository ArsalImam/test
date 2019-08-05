package com.bykea.pk.partner.ui.withdraw

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bykea.pk.partner.R
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * This class will responsible to show withdrawal summary on a dialog
 *
 * @author Arsal Imam
 */
open class DialogWithdrawConfirmation : BottomSheetDialog {

    /**
     * viewModel of this view
     */
    private var withdrawalViewModel: WithdrawalViewModel? = null

    /**
     * contructor to create new instance of this class
     */
    constructor(context: Context) : super(context)

    /**
     * Setter of viewModel object
     */
    private fun setViewModel(withdrawalViewModel: WithdrawalViewModel) {
        this.withdrawalViewModel = withdrawalViewModel
    }

    /**
     * Invoke by system, when this dialog creates
     *
     * @param savedInstanceState obtained data sent by callee
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_withdraw_confirmation)

        val dismissTextView = findViewById<View>(R.id.dismiss_withdraw_txtview)
        val confirmTextView = findViewById<View>(R.id.confirm_withdraw_txtview)

        val totalTextView = findViewById<TextView>(R.id.confirmation_total_value)
        val feesTextView = findViewById<TextView>(R.id.confirmation_fees_value)
        val amountTextView = findViewById<TextView>(R.id.confirmation_payment_value)

        val amount = withdrawalViewModel!!.balanceInt.value!!
        val fees = Math.round(withdrawalViewModel!!.selectedPaymentMethod!!.fees!!)

        amountTextView!!.text = amount.toString()
        feesTextView!!.text = String.format("-%s", fees)
        totalTextView!!.text = (amount + fees).toString()

        dismissTextView!!.setOnClickListener { withdrawalViewModel!!.showConfirmationDialog(false) }
        confirmTextView!!.setOnClickListener {
            withdrawalViewModel!!.showConfirmationDialog(false)
            withdrawalViewModel!!.confirmWithdraw()
        }
    }

    companion object {

        /**
         * This will return a new instance of this class
         *
         * @param withdrawalActivity context to launch new instance
         * @param withdrawalViewModel viewModel
         */
        fun newInstance(withdrawalActivity: WithdrawalActivity,
                        withdrawalViewModel: WithdrawalViewModel): DialogWithdrawConfirmation {
            val dialogWithDrawConfirmation = DialogWithdrawConfirmation(withdrawalActivity)
            dialogWithDrawConfirmation.setViewModel(withdrawalViewModel)
            return dialogWithDrawConfirmation
        }
    }
}