package com.bykea.pk.partner.ui.support


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.adapters.ProblemItemsAdapter
import com.bykea.pk.partner.utils.Utils
import kotlinx.android.synthetic.main.fragment_complain_reason.*
import java.util.*
import kotlin.collections.ArrayList

class ComplainReasonFragment : Fragment() {

    private var mCurrentActivity: ComplaintSubmissionActivity? = null
    private var mAdapter: ProblemItemsAdapter? = null
    private var mLayoutManager: LinearLayoutManager? = null

    private var complainReasonsAdapterList: ArrayList<String> = ArrayList()
    private lateinit var rideOrGeneralComplainReasonsList: Array<String>
    private lateinit var financeComplainReasonsList: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_complain_reason, container, false)
        mCurrentActivity = activity as ComplaintSubmissionActivity?
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        complainReasonsAdapterList = ArrayList()
        if (mCurrentActivity?.tripHistoryDate != null) {
            //CREATE TICKET FOR RIDE REASONS
            rideOrGeneralComplainReasonsList = AppPreferences.getSettings().predefine_messages.reasons
        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            rideOrGeneralComplainReasonsList = AppPreferences.getSettings().predefine_messages.contact_reason
            financeComplainReasonsList = AppPreferences.getSettings().predefine_messages.contact_reason_finance
        }

        cloneReasonsList()

        if (!complainReasonsAdapterList.isEmpty())
            setupAdapter()
    }

    /**
     * Copy Reasons To List (Retrieve From App Preferences)
     */
    private fun cloneReasonsList() {
        if (mCurrentActivity?.tripHistoryDate != null) {
            //CREATE TICKET FOR RIDE REASONS
            if (!rideOrGeneralComplainReasonsList.isNullOrEmpty()) {
                complainReasonsAdapterList.addAll(rideOrGeneralComplainReasonsList)
            } else {
                complainReasonsAdapterList.addAll(Utils.getRideComplainReasonsList(mCurrentActivity))
            }
        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            if (!rideOrGeneralComplainReasonsList.isNullOrEmpty())
                complainReasonsAdapterList.addAll(rideOrGeneralComplainReasonsList)

            if (!financeComplainReasonsList.isNullOrEmpty())
                complainReasonsAdapterList.addAll(financeComplainReasonsList)
        }
    }

    /**
     * Setup Adapter and Set Listener
     */
    private fun setupAdapter() {
        mAdapter = ProblemItemsAdapter(complainReasonsAdapterList, mCurrentActivity)
        mLayoutManager = LinearLayoutManager(mCurrentActivity)
        rvProblemList?.layoutManager = mLayoutManager
        rvProblemList?.itemAnimator = DefaultItemAnimator()
        rvProblemList?.adapter = mAdapter

        mAdapter?.setMyOnItemClickListener { position, view, reason ->
            mCurrentActivity?.selectedReason = reason
            mCurrentActivity?.changeFragment(ComplainDetailFragment())
        }
    }
}