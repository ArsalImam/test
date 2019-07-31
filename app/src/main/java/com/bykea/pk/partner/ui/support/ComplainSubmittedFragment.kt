package com.bykea.pk.partner.ui.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.FragmentComplainSubmittedBinding
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import kotlinx.android.synthetic.main.fragment_complain_submitted.*
import org.apache.commons.lang3.StringUtils

class ComplainSubmittedFragment : Fragment() {

    private lateinit var mCurrentActivity: ComplaintSubmissionActivity
    private lateinit var binding: FragmentComplainSubmittedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complain_submitted, container, false)
        mCurrentActivity = activity as ComplaintSubmissionActivity

        mCurrentActivity.setBackButtonVisibility(View.GONE)

        binding.listener = object : GenericFragmentListener {
            override fun onRequestSubmittedTickets() {
                ActivityStackManager.getInstance().startComplainListActivity(activity)
            }

            override fun onNavigateToHomeScreen() {
                ActivityStackManager.getInstance().startHomeActivity(mCurrentActivity)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tVIssueNumber.text = StringBuilder().append(context?.getString(R.string.issue_no)).append(StringUtils.SPACE).append(context?.getString(R.string.issue_submitted))
    }
}
