package com.bykea.pk.partner.ui.withdraw;

import android.os.Bundle;
import android.view.View;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.BaseActivity;

public class WithdrawThankyouActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_thankyou);

        View mWalletTextView = findViewById(R.id.withdraw_thankyou_wallet);
        mWalletTextView.setOnClickListener(v -> finish());
    }
}