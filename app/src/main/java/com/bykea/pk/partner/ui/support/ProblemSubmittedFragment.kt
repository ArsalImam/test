package com.bykea.pk.partner.ui.support

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.FragmentProblemSubmittedBinding
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Utils
import kotlinx.android.synthetic.main.fragment_problem_submitted.*

class ProblemSubmittedFragment : Fragment() {

    private lateinit var mCurrentActivity: ProblemActivity
    private lateinit var rootView: View
    private lateinit var binding: FragmentProblemSubmittedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problem_submitted, container, false)
        rootView = binding.root
        mCurrentActivity = activity as ProblemActivity

        mCurrentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.listener = object : GenericFragmentListener {
            override fun onRequestSubmittedTickets() {
                Utils.appToast(mCurrentActivity, "submittedIssueDetail")
//                HelpCenterActivity.builder().show(mCurrentActivity)
                startActivity(Intent(activity, ComplainListActivity::class.java))
            }

            override fun onNavigateToHomeScreen() {
                ActivityStackManager.getInstance().startHomeActivity(mCurrentActivity)
            }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tVIssueNumber.text = context?.getString(R.string.issue_no) + " " + context?.getString(R.string.issue_submitted)
    }
}
