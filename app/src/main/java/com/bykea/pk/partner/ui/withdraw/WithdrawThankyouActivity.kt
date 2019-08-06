package com.bykea.pk.partner.ui.withdraw

import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.activities.BaseActivity

class WithdrawThankyouActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_thankyou)

        val walletTextView = findViewById<View>(R.id.withdraw_thankyou_wallet)
        val walletDescriptionTextView = findViewById<TextView>(R.id.description)
        walletDescriptionTextView.text = getString(R.string.thanks_details_1) + "\n" + getString(R.string.thanks_details_2)
        walletTextView.setOnClickListener { finish() }

    }
}