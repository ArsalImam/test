package com.bykea.pk.partner.ui.withdraw

import android.os.Bundle
import android.view.View

import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.activities.BaseActivity

class WithdrawThankyouActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_thankyou)

        val mWalletTextView = findViewById<View>(R.id.withdraw_thankyou_wallet)
        mWalletTextView.setOnClickListener { v -> finish() }
    }
}