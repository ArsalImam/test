package com.bykea.pk.partner.ui.complain


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.data.ComplainReason
import com.bykea.pk.partner.dal.source.remote.response.ComplainReasonResponse
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.dal.util.MESSAGE_TYPE
import com.bykea.pk.partner.databinding.FragmentComplainReasonBinding
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.adapters.ProblemItemsAdapter
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.TelloTalkManager
import com.bykea.pk.partner.utils.Utils
import com.zendesk.util.StringUtils
import kotlinx.android.synthetic.main.fragment_complain_reason.*

/**
 * Use For The Selection Of Reasons For Ticket Submission.
 */
class ComplainReasonFragment : Fragment() {

    private var mCurrentActivity: ComplaintSubmissionActivity? = null
    private var mAdapter: ProblemItemsAdapter? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var jobsRepository: JobsRepository? = null

    private var complainReasonsAdapterList: ArrayList<ComplainReason> = ArrayList()
    private lateinit var rideOrGeneralComplainReasonsList: List<ComplainReason>
    private lateinit var financeComplainReasonsList: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentComplainReasonBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_complain_reason, container, false)

        mCurrentActivity = activity as ComplaintSubmissionActivity?
        jobsRepository = Injection.provideJobsRepository(mCurrentActivity!!)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        complainReasonsAdapterList = ArrayList()
        if (mCurrentActivity?.tripHistoryDate != null) {
            //CREATE TICKET FOR RIDE REASONS
            Dialogs.INSTANCE.showLoader(mCurrentActivity)
            jobsRepository?.getJobComplainReasons(MESSAGE_TYPE, object : JobsDataSource.ComplainReasonsCallback {
                override fun onSuccess(complainReasonResponse: ComplainReasonResponse) {
                    Dialogs.INSTANCE.dismissDialog()
                    rideOrGeneralComplainReasonsList = complainReasonResponse?.data!!
                    cloneReasonsList()

                    if (!complainReasonsAdapterList.isEmpty())
                        setupAdapter()
                }

                override fun onFail(code: Int, subCode: Int?, message: String?) {
                    super.onFail(code, subCode, message)
                    Dialogs.INSTANCE.dismissDialog()
                    Utils.appToast(mCurrentActivity?.getString(R.string.error_try_again))
                }
            })

        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            rideOrGeneralComplainReasonsList = mapStringListToComplainReasons(AppPreferences.getSettings().predefine_messages.contact_reason)
            financeComplainReasonsList = AppPreferences.getSettings().predefine_messages.contact_reason_finance
            cloneReasonsList()

            if (!complainReasonsAdapterList.isEmpty())
                setupAdapter()
        }
    }

    /**
     * this map will map string list of reasons to the [ComplainReason] list
     * @param messages to convert into it
     */
    private fun mapStringListToComplainReasons(messages: Array<String>): List<ComplainReason> {
        return messages.map { ComplainReason(StringUtils.EMPTY_STRING, it, StringUtils.EMPTY_STRING) }
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
                complainReasonsAdapterList.addAll(mapStringListToComplainReasons(Utils.getRideComplainReasonsList(mCurrentActivity)))
            }
        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            if (!rideOrGeneralComplainReasonsList.isNullOrEmpty())
                complainReasonsAdapterList.addAll(rideOrGeneralComplainReasonsList)

            if (!financeComplainReasonsList.isNullOrEmpty())
                complainReasonsAdapterList.addAll(mapStringListToComplainReasons(financeComplainReasonsList))
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

        mAdapter?.setMyOnItemClickListener { _, _, reason ->
            mCurrentActivity?.selectedReason = reason
            var template = reason?.message!!
            mCurrentActivity?.tripHistoryDate?.let {
                template = String.format(mCurrentActivity?.getString(R.string.tello_trip_template)!!, it?.tripNo, reason.message)
            }
            TelloTalkManager.instance().openCorporateChat(activity, template, null)
        }
    }

    /**
     * Check Is Email Is Updated
     */
    private fun checkIsEmailUpdatedFromRemoteDataSource() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity)
        jobsRepository?.checkEmailUpdate(object : JobsDataSource.EmailUpdateCheckCallback {
            override fun onSuccess(isEmailUpdated: Boolean?) {
                Dialogs.INSTANCE.dismissDialog()
                if (isEmailUpdated != null && isEmailUpdated) {
                    AppPreferences.setEmailVerified()
                    ActivityStackManager.getInstance().startComplainAddActivity(mCurrentActivity!!,
                            mCurrentActivity?.tripHistoryDate!!, mCurrentActivity?.selectedReason!!)
                } else {
                    mCurrentActivity?.signIn()
                }
            }

            override fun onFail(message: String?) {
                Dialogs.INSTANCE.dismissDialog()
                Utils.appToast(mCurrentActivity?.getString(R.string.error_try_again))
            }
        })
    }
}