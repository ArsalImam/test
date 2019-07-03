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
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel

import kotlinx.android.synthetic.main.activity_problem.*


class ProblemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProblemBinding
    private lateinit var mCurrentActivity: ProblemActivity
    private var fragmentManager: FragmentManager? = null

    private var tripId: String? = null
    var selectedReason: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_problem)
        mCurrentActivity = this

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        tripId = intent.getStringExtra("TRIP_ID")
        fragmentManager = supportFragmentManager
        changeFragment(ProblemListFragment(), LIST_FRAGMENT)

        binding.viewmodel = obtainViewModel(ProblemViewModel::class.java)
        binding.listener = object : ProblemListener {

        }
    }

    fun changeFragment(fragment: Fragment, fragmentTag: String) {
        fragmentManager!!
                .beginTransaction()
                .replace(R.id.containerView, fragment, fragmentTag)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        val DETAIL_FRAGMENT: String = "DETAIL_FRAGMENT"
        val LIST_FRAGMENT: String = "LIST_FRAGMENT"
    }
}
