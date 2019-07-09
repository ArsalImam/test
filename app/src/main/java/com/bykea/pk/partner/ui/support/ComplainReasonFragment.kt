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
import kotlinx.android.synthetic.main.fragment_complain_reason.*
import java.util.*

class ComplainReasonFragment : Fragment() {

    private var mCurrentActivity: ComplaintSubmissionActivity? = null
    private var mAdapter: ProblemItemsAdapter? = null
    private var mProblemList: ArrayList<String> = ArrayList()
    private var mLayoutManager: LinearLayoutManager? = null
    internal var probReasons1: Array<String>? = null
    internal var probReasons2: Array<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_complain_reason, container, false)
        mCurrentActivity = activity as ComplaintSubmissionActivity?
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mProblemList = ArrayList()
        if (mCurrentActivity?.tripHistoryDate != null) {
            probReasons1 = AppPreferences.getSettings().predefine_messages.reasons
        } else {
            probReasons1 = AppPreferences.getSettings().predefine_messages.contact_reason
            probReasons2 = AppPreferences.getSettings().predefine_messages.contact_reason_finance
        }

        cloneReasonsList()
        setupAdapter()
    }

    /**
     * Copy Reasons To List (Retrieve From App Preferences)
     */
    private fun cloneReasonsList() {
        if (mCurrentActivity?.tripHistoryDate != null) {
            //CREATE TICKET FOR RIDE REASONS
            if (probReasons1 != null) {
                mProblemList.addAll(probReasons1!!)
            } else {
                mProblemList.add("Partner ka ravaiya gair ikhlaqi tha")
                mProblemList.add("Partner ne aik gair mansooba stop kia")
                mProblemList.add("Partner ne khud safar ka aghaz kar k ikhtitam kardia, mere pas ai baghair).")
                mProblemList.add("Partner ne khud safar ka aghaz kar k ikhtitam kardia, mere pas ai baghair).")
                mProblemList.add("Parner ne baqaya raqam mere wallet me nahi daali")
                mProblemList.add("main haadse main manavas tha.")
            }
        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            if (probReasons1 != null)
                mProblemList.addAll(probReasons1!!)

            if (probReasons2 != null)
                mProblemList.addAll(probReasons2!!)
        }
    }

    /**
     * Setup Adapter and Set Listener
     */
    private fun setupAdapter() {
        mAdapter = ProblemItemsAdapter(mProblemList, mCurrentActivity)
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