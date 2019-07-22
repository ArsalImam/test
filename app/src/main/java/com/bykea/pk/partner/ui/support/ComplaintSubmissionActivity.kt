package com.bykea.pk.partner.ui.support

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobRequestsDataSource
import com.bykea.pk.partner.dal.source.JobRequestsRepository
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.databinding.ActivityProblemBinding
import com.bykea.pk.partner.models.data.TripHistoryData
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Constants.INTENT_TRIP_HISTORY_DATA
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Keys
import com.bykea.pk.partner.utils.Utils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_problem.*
import java.util.*


class ComplaintSubmissionActivity : BaseActivity() {

    private lateinit var binding: ActivityProblemBinding
    private lateinit var mCurrentActivity: ComplaintSubmissionActivity
    private var fragmentManager: FragmentManager? = null
    private lateinit var jobRequestsRepository: JobRequestsRepository

    internal var isTicketSubmitted: Boolean = false
    var tripHistoryDate: TripHistoryData? = null

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001

    var selectedReason: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_problem)
        mCurrentActivity = this
        jobRequestsRepository = Injection.provideBookingsRepository(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (intent?.extras != null) {
            if (intent.extras.containsKey(INTENT_TRIP_HISTORY_DATA))
                tripHistoryDate = intent.getSerializableExtra(INTENT_TRIP_HISTORY_DATA) as TripHistoryData
        }

        if (tripHistoryDate != null && tripHistoryDate?.tripNo != null) {
            //CREATE TICKET FOR RIDE REASONS
            toolbar_title.text = tripHistoryDate?.tripNo
        } else {
            //CREATE TICKET FOR FINANCIAL AND SUPERVISOR REASONS
            toolbar_title.text = getString(R.string.title_report)
        }

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
                .add(R.id.containerView, fragment)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .addToBackStack(null)
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
        jobRequestsRepository.getEmailUpdate(emailId, object : JobRequestsDataSource.EmailUpdateCallback {
            override fun onSuccess() {
                Dialogs.INSTANCE.dismissDialog()
                AppPreferences.setDriverEmail(emailId)
                AppPreferences.setEmailVerified()
                mGoogleSignInClient?.signOut()
                checkStatusForZendesk()
            }

            override fun onFail(message: String?) {
                Dialogs.INSTANCE.dismissDialog()
                Utils.appToast(this@ComplaintSubmissionActivity, getString(R.string.error_try_again))
            }
        })
    }

    /**
     * Check Status For Zendesk SDK
     * If Ready : Open Detail Fragment - To Submit Ticket
     * If Not : Open Zendesk Identity Activity
     */
    internal fun checkStatusForZendesk() {
        if (AppPreferences.isZendeskSDKReady()) {
            //IF ZENDESK SDK IS READY
            changeFragment(ComplainDetailFragment())
        } else {
            if (!AppPreferences.checkKeyExist(Keys.ZENDESK_IDENTITY_SETUP_TIME)) {
                //INTIALIZE ZENDESK SDK
                Utils.setZendeskIdentity()
                AppPreferences.setZendeskSDKSetupTime()
                ActivityStackManager.getInstance().startZendeskIdentityActivity(mCurrentActivity)
            } else {
                if ((Date().time - AppPreferences.getZendeskSDKSetupTime().time) < Constants.ZendeskConfigurations.ZENDESK_SETTING_IDENTITY_MAX_TIME) {
                    //ZENDESK SDK NOT READY
                    ActivityStackManager.getInstance().startZendeskIdentityActivity(mCurrentActivity)
                } else {
                    //ZENDESK SDK IS READY
                    AppPreferences.setZendeskSDKReady()
                    changeFragment(ComplainDetailFragment())
                }
            }
        }
    }
}
