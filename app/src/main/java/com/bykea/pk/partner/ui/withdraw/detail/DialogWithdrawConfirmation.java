package com.bykea.pk.partner.ui.withdraw.detail;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

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
        dismissTextView.setOnClickListener(v -> withdrawalViewModel.getShowConfirmationDialog().setValue(false));
        confirmTextView.setOnClickListener(v -> {
            withdrawalViewModel.getShowConfirmationDialog().setValue(false);
            withdrawalViewModel.confirmWithdraw();
        });
    }
}