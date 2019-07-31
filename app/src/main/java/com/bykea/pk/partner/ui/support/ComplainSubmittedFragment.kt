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
import com.bykea.pk.partner.ui.helpers.FontUtils
import kotlinx.android.synthetic.main.fragment_complain_submitted.*
import org.apache.commons.lang3.StringUtils
import android.text.SpannableStringBuilder


class ComplainSubmittedFragment : Fragment() {

    private lateinit var mCurrentActivity: ComplaintSubmissionActivity
    private lateinit var binding: FragmentComplainSubmittedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complain_submitted, container, false)
        mCurrentActivity = activity as ComplaintSubmissionActivity

        mCurrentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

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

        if (mCurrentActivity.tripHistoryDate != null) {
            tVIssueNumber.text = SpannableStringBuilder("")
                    .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.you_complain), "jameel_noori_nastaleeq.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, StringUtils.SPACE, "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, mCurrentActivity.tripHistoryDate?.tripNo, "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, StringUtils.SPACE, "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(mCurrentActivity, context?.getString(R.string.issue_submitted), "jameel_noori_nastaleeq.ttf"))
        } else {
            tVIssueNumber.text = StringBuilder().append(context?.getString(R.string.you_complain))
                    .append(StringUtils.SPACE).append(context?.getString(R.string.issue_submitted))
        }
    }
}
