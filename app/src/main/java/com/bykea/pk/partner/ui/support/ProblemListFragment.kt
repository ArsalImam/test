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
import kotlinx.android.synthetic.main.fragment_problem_list.*
import java.util.*

class ProblemListFragment : Fragment() {

    private var mCurrentActivity: ProblemActivity? = null
    private var mAdapter: ProblemItemsAdapter? = null
    private var mProblemList: ArrayList<String> = ArrayList()
    private var mLayoutManager: LinearLayoutManager? = null
    internal var probReasons: Array<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_problem_list, container, false)
        mCurrentActivity = activity as ProblemActivity?
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mProblemList = ArrayList()
        probReasons = AppPreferences.getSettings().predefine_messages.reasons

        cloneReasonsList()
        setupAdapter()
    }

    /**
     * Copy Reasons To List (Retrieve From App Preferences)
     */
    private fun cloneReasonsList() {
        if (probReasons != null) {
            mProblemList.addAll(probReasons!!)
        } else {
            mProblemList.add("Partner ka ravaiya gair ikhlaqi tha")
            mProblemList.add("Partner ne aik gair mansooba stop kia")
            mProblemList.add("Partner ne khud safar ka aghaz kar k ikhtitam kardia, mere pas ai baghair).")
            mProblemList.add("Partner ne khud safar ka aghaz kar k ikhtitam kardia, mere pas ai baghair).")
            mProblemList.add("Parner ne baqaya raqam mere wallet me nahi daali")
            mProblemList.add("main haadse main manavas tha.")
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
            mCurrentActivity?.changeFragment(ProblemDetailFragment())
        }
    }
}