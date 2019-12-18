package com.bykea.pk.partner.ui.complain

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.databinding.ActivityProblemBinding
import com.bykea.pk.partner.models.data.TripHistoryData
import com.bykea.pk.partner.models.response.TripHistoryResponse
import com.bykea.pk.partner.repositories.UserDataHandler
import com.bykea.pk.partner.repositories.UserRepository
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.FontUtils
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.INTENT_TRIP_HISTORY_DATA
import com.bykea.pk.partner.utils.Constants.INTENT_TRIP_HISTORY_ID
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_problem.*
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils


class ComplaintSubmissionActivity : BaseActivity() {

    private lateinit var binding: ActivityProblemBinding
    private lateinit var mCurrentActivity: ComplaintSubmissionActivity
    private var fragmentManager: FragmentManager? = null
    private lateinit var jobRespository: JobsRepository

    internal var isTicketSubmitted: Boolean = false
    var tripHistoryDate: TripHistoryData? = null

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001

    var selectedReason: String? = null
    var tripHistoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_problem)
        mCurrentActivity = this
        jobRespository = Injection.provideJobsRepository(this)

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayShowTitleEnabled(false)

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        if (intent?.extras != null) {
            if (intent.extras.containsKey(INTENT_TRIP_HISTORY_DATA))
                tripHistoryDate = intent.getSerializableExtra(INTENT_TRIP_HISTORY_DATA) as TripHistoryData
            if (intent.extras.containsKey(INTENT_TRIP_HISTORY_ID)) {
                tripHistoryId = intent.getStringExtra(INTENT_TRIP_HISTORY_ID)
                updateTripDetailsById(tripHistoryId!!)
                return
            }
        }
        updateUi()
    }


    /**
     * this method can be used to get all trips history from driver id
     * this is a legacy function and is replaced with {@link #requestBookingListing(Context, IUserDataHandler, String, String)}
     * and will be removed in the future release
     *
     * @param tripHistoryId (optional) if any specific trip details needed
     */
    private fun updateTripDetailsById(tripHistoryId: String) {
        Dialogs.INSTANCE.showLoader(this@ComplaintSubmissionActivity)
        UserRepository().requestTripHistory(this@ComplaintSubmissionActivity, object : UserDataHandler() {
            override fun onGetTripHistory(tripHistoryResponse: TripHistoryResponse?) {
                super.onGetTripHistory(tripHistoryResponse)
                Dialogs.INSTANCE.dismissDialog()
                if (tripHistoryResponse?.isSuccess!! && CollectionUtils.isNotEmpty(tripHistoryResponse?.data)) {
                    tripHistoryDate = tripHistoryResponse.data[Constants.DIGIT_ZERO]
                    updateUi()
                } else {
                    Utils.appToast(tripHistoryResponse?.message)
                }
            }
        }, Constants.DIGIT_ONE.toString(), tripHistoryId)
    }

    private fun updateUi() {
        if (tripHistoryDate != null && tripHistoryDate?.tripNo != null) {
            //CREATE TICKET FOR RIDE REASONS
            toolbar_title.text = tripHistoryDate?.tripNo
        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            toolbar_title.text = SpannableStringBuilder(StringUtils.EMPTY)
                    .append(StringUtils.SPACE)
                    .append(FontUtils.getStyledTitle(this@ComplaintSubmissionActivity, getString(R.string.title_report_ur), "jameel_noori_nastaleeq.ttf"))
                    .append(FontUtils.getStyledTitle(this@ComplaintSubmissionActivity, StringUtils.SPACE, "roboto_medium.ttf"))
                    .append(FontUtils.getStyledTitle(this@ComplaintSubmissionActivity, getString(R.string.title_report_en), "roboto_medium.ttf"))
                    .append(StringUtils.SPACE)
        }


        fragmentManager = supportFragmentManager
        changeFragment(ComplainReasonFragment())
    }

    /**
     * Use For Change Fragment
     */
    fun changeFragment(fragment: Fragment) {
        fragmentManager!!
                .beginTransaction()
                .add(R.id.containerView, fragment)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .addToBackStack(null)
                .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1 && !isTicketSubmitted) {
            supportFragmentManager.popBackStack()
        } else if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        }
    }

    /**
     * Create GoogleSignInClient, Dialog For Account Selection
     */
    internal fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        startActivityForResult(mGoogleSignInClient?.getSignInIntent(), RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task?.getResult(ApiException::class.java)?.email?.let {
                    updateEmailFromRemoteDataSource(it)
                }
            } catch (e: ApiException) {
            }
        }
    }

    /**
     * @param emailId : Driver Valid Email Id
     */
    private fun updateEmailFromRemoteDataSource(emailId: String) {
        Dialogs.INSTANCE.showLoader(this@ComplaintSubmissionActivity)
        jobRespository.getEmailUpdate(emailId, object : JobsDataSource.EmailUpdateCallback {
            override fun onSuccess() {
                Dialogs.INSTANCE.dismissDialog()
                AppPreferences.setDriverEmail(emailId)
                AppPreferences.setEmailVerified()
                mGoogleSignInClient?.signOut()
                changeFragment(ComplainDetailFragment())
            }

            override fun onFail(message: String?) {
                Dialogs.INSTANCE.dismissDialog()
                Dialogs.INSTANCE.showToast(getString(R.string.error_try_again))
            }
        })
    }
}