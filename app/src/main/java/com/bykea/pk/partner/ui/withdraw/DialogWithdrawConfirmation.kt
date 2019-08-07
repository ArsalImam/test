package com.bykea.pk.partner.ui.withdraw

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.DialogWithdrawConfirmationBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * This class will responsible to show withdrawal summary on a dialog
 *
 * @author Arsal Imam
 */
open class DialogWithdrawConfirmation : BottomSheetDialog {

    /**
     * view binder all ui components present in xml
     */
    private var viewBinder: DialogWithdrawConfirmationBinding? = null

    /**
     * viewModel of this view
     */
    private var withdrawalViewModel: WithdrawalViewModel? = null

    /**
     * contructor to create new instance of this class
     */
    constructor(activity: Activity) : super(activity) {
        initUi(activity = activity)
    }

    /**
     * this method will initialize the bottom sheet UI components
     */
    private fun initUi(activity: Activity) {

        viewBinder = DataBindingUtil.inflate(activity.layoutInflater, R.layout.dialog_withdraw_confirmation,
                        null, false)

        ownerActivity = activity
        window.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(viewBinder?.root)

        viewBinder?.dismissWithdrawTxtview?.setOnClickListener { withdrawalViewModel?.showConfirmationDialog(false) }
        viewBinder?.confirmWithdrawTxtview?.setOnClickListener {
            withdrawalViewModel?.showConfirmationDialog(false)
            withdrawalViewModel?.confirmWithdraw()
        }
    }

    /**
     * Setter of viewModel object
     */
    private fun setViewModel(withdrawalViewModel: WithdrawalViewModel?) {
        this.withdrawalViewModel = withdrawalViewModel
    }

    /**
     * open dialog and update the content of the dialog
     */
    fun showDialog() {
        show()
        updateContent()
    }

    /**
     * update dialog everytime on popup opens...
     */
    private fun updateContent() {
        if (viewBinder == null) return

        val amount = withdrawalViewModel?.balanceInt?.value!!
        val fees = Math.round(withdrawalViewModel?.selectedPaymentMethod?.fees!!)

        viewBinder!!.confirmationPaymentValue?.text =
                String.format(ownerActivity.getString(R.string.formatted_price), amount)

        viewBinder!!.confirmationFeesValue?.text =
                String.format(ownerActivity.getString(R.string.specifier_string), fees)

        viewBinder!!.confirmationTotalValue?.text =
                String.format(ownerActivity.getString(R.string.rs_price), (amount - fees))
    }

    companion object {

        /**
         * This will return a new instance of this class
         *
         * @param withdrawalActivity context to launch new instance
         * @param withdrawalViewModel viewModel
         */
        fun newInstance(withdrawalActivity: WithdrawalActivity,
                        withdrawalViewModel: WithdrawalViewModel?): DialogWithdrawConfirmation {
            val dialogWithDrawConfirmation = DialogWithdrawConfirmation(withdrawalActivity)
            dialogWithDrawConfirmation.setViewModel(withdrawalViewModel)
            return dialogWithDrawConfirmation
        }
    }
}