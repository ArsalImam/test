package com.bykea.pk.partner.ui.withdraw

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityWithdrawThankyouBinding
import com.bykea.pk.partner.ui.activities.BaseActivity

/**
 * This class will responsible to manage the withdrawal thank you screen
 *
 * @author Arsal Imam
 */
class WithdrawThankyouActivity : BaseActivity() {

    private var viewBinder: ActivityWithdrawThankyouBinding? = null

    /**
     * {@inheritDoc}
     *
     * This will calls on every new initialization of this activity,
     * It can be used for any initializations or on start executions
     *
     * @param savedInstanceState to get data on activity state changed
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinder = DataBindingUtil.setContentView(this, R.layout.activity_withdraw_thankyou)
        viewBinder?.description?.text = getString(R.string.thanks_details_1) + getString(R.string.new_line) + getString(R.string.thanks_details_2)
        viewBinder?.withdrawThankyouWallet?.setOnClickListener { finish() }
    }
}