package com.bykea.pk.partner.ui.withdraw

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.bykea.pk.partner.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class DialogWithdrawConfirmation : BottomSheetDialog {
    private var withdrawalViewModel: WithdrawalViewModel? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, theme: Int) : super(context, theme) {}


    protected constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener) {}

    private fun setViewModel(withdrawalViewModel: WithdrawalViewModel) {
        this.withdrawalViewModel = withdrawalViewModel
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_withdraw_confirmation)

        val dismissTextView = findViewById<View>(R.id.dismiss_withdraw_txtview)
        val confirmTextView = findViewById<View>(R.id.confirm_withdraw_txtview)

        val totalTextView = findViewById<TextView>(R.id.confirmation_total_value)
        val feesTextView = findViewById<TextView>(R.id.confirmation_fees_value)
        val amountTextView = findViewById<TextView>(R.id.confirmation_payment_value)

        val amount = withdrawalViewModel!!.balanceToWithdraw.value!!
        val fees = Math.round(withdrawalViewModel!!.selectedPaymentMethod!!.fees!!)

        amountTextView!!.text = amount.toString()
        feesTextView!!.text = String.format("-%s", fees)
        totalTextView!!.text = (amount + fees).toString()

        dismissTextView!!.setOnClickListener { v -> withdrawalViewModel!!.getShowConfirmationDialog().setValue(false) }
        confirmTextView!!.setOnClickListener { v ->
            withdrawalViewModel!!.getShowConfirmationDialog().setValue(false)
            withdrawalViewModel!!.confirmWithdraw()
        }
    }

    companion object {

        fun newInstance(withdrawalActivity: WithdrawalActivity,
                        withdrawalViewModel: WithdrawalViewModel): DialogWithdrawConfirmation {
            val dialogWithDrawConfirmation = DialogWithdrawConfirmation(withdrawalActivity)
            dialogWithDrawConfirmation.setViewModel(withdrawalViewModel)
            return dialogWithDrawConfirmation
        }
    }
}