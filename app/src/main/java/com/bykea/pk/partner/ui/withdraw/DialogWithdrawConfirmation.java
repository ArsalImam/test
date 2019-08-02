package com.bykea.pk.partner.ui.withdraw;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bykea.pk.partner.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DialogWithdrawConfirmation extends BottomSheetDialog {
    private WithdrawalViewModel withdrawalViewModel;

    public DialogWithdrawConfirmation(@NonNull Context context) {
        super(context);
    }

    public DialogWithdrawConfirmation(@NonNull Context context, int theme) {
        super(context, theme);
    }


    protected DialogWithdrawConfirmation(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static DialogWithdrawConfirmation newInstance(WithdrawalActivity withdrawalActivity,
                                                         WithdrawalViewModel withdrawalViewModel) {
        DialogWithdrawConfirmation dialogWithDrawConfirmation = new DialogWithdrawConfirmation(withdrawalActivity);
        dialogWithDrawConfirmation.setViewModel(withdrawalViewModel);
        return dialogWithDrawConfirmation;
    }

    private void setViewModel(WithdrawalViewModel withdrawalViewModel) {
        this.withdrawalViewModel = withdrawalViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_withdraw_confirmation);

        View dismissTextView = findViewById(R.id.dismiss_withdraw_txtview);
        View confirmTextView = findViewById(R.id.confirm_withdraw_txtview);

        TextView totalTextView = findViewById(R.id.confirmation_total_value);
        TextView feesTextView = findViewById(R.id.confirmation_fees_value);
        TextView amountTextView = findViewById(R.id.confirmation_payment_value);

        int amount = withdrawalViewModel.getBalanceToWithdraw().getValue();
        long fees = Math.round(withdrawalViewModel.getSelectedPaymentMethod().getFees());

        amountTextView.setText(String.valueOf(amount));
        feesTextView.setText(String.format("-%s", fees));
        totalTextView.setText(String.valueOf(amount + fees));

        dismissTextView.setOnClickListener(v -> withdrawalViewModel.getShowConfirmationDialog().setValue(false));
        confirmTextView.setOnClickListener(v -> {
            withdrawalViewModel.getShowConfirmationDialog().setValue(false);
            withdrawalViewModel.confirmWithdraw();
        });
    }
}