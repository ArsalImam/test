package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;


import com.bykea.pk.partner.R;

import butterknife.ButterKnife;

public class PaymentRequestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);
        setToolbarTitle("Payment Request");
        hideToolbarLogo();
        setBackNavigation();
    }
}
