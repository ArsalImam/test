package com.bykea.pk.partner.ui.support

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityProblemBinding
import com.bykea.pk.partner.models.data.TripHistoryData
import com.bykea.pk.partner.utils.Constants.INTENT_TRIP_HISTORY_DATA

import kotlinx.android.synthetic.main.activity_problem.*


class ComplaintSubmissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProblemBinding
    private lateinit var mCurrentActivity: ComplaintSubmissionActivity
    private var fragmentManager: FragmentManager? = null

    var tripHistoryDate: TripHistoryData? = null

    var selectedReason: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_problem)
        mCurrentActivity = this

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        tripHistoryDate = intent.getSerializableExtra(INTENT_TRIP_HISTORY_DATA) as TripHistoryData
        toolbar_title.text = tripHistoryDate?.tripNo

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        fragmentManager = supportFragmentManager
        changeFragment(ComplainReasonFragment())
    }

    /**
     * Use For Change Fragment
     */
    fun changeFragment(fragment: Fragment) {
        fragmentManager!!
                .beginTransaction()
                .replace(R.id.containerView, fragment)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commit()
    }

    /**
     * Trigger When Toolbar Back Button Is Tapped
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        //STOP BACK PRESSED FOR PROBLEM SUBMITTED FRAGMENT
        if (!supportFragmentManager.fragments.get(0).javaClass.simpleName.equals(ComplainSubmittedFragment::class.java.simpleName)) {
            super.onBackPressed()
        }
    }
}
