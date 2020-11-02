package com.bykea.pk.partner.ui.withdraw

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod
import com.bykea.pk.partner.databinding.ActivityWithDrawalBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.FontUtils
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import org.apache.commons.lang3.StringUtils
import java.util.*
import kotlin.math.roundToLong


/**
 * This class will responsible to manage the complete withdrawal process
 *
 * @author Arsal Imam
 */
class   WithdrawalActivity : BaseActivity() {

    /**
     * Binding object between activity and xml file, it contains all objects
     * of UI components used by activity
     */
    private var binding: ActivityWithDrawalBinding? = null

    /**
     * ViewModel object of {WithdrawalActivity} View
     */
    private var viewModel: WithdrawalViewModel? = null

    /**
     * Datasource object to populate payment methods in recyclerview
     */
    private var adapter: WithdrawalPaymentMethodsAdapter? = null

    /**
     * Confirmation dialog object
     */
    private var confirmationDialog: DialogWithdrawConfirmation? = null

    /**
     * {@inheritDoc}
     *
     *
     * This will calls on every new initialization of this activity,
     * It can be used for any initializations or on start executions
     *
     * @param savedInstanceState to get data on activity state changed
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_with_drawal)

        viewModel = this.obtainViewModel(WithdrawalViewModel::class.java)
        binding?.viewmodel = viewModel

        confirmationDialog = DialogWithdrawConfirmation.newInstance(this, viewModel)

        initUi()
        setupObservers()
        setupRecyclerView()
    }

    /**
     * This method is binding view model properties with view components
     * through LiveData API available in Android MVVM
     *
     * @see [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
     */
    private fun setupObservers() {
        viewModel?.showLoader?.observe(this, Observer { it ->
            if (it)
                Dialogs.INSTANCE.showLoader(this@WithdrawalActivity)
            else
                Dialogs.INSTANCE.dismissDialog()
        })

        viewModel?.driverProfile?.observe(this, Observer { it ->
            if (it != null) {

                val spannableStringBuilder =
                        FontUtils.getStyledTitle(this@WithdrawalActivity,
                                String.format(getString(R.string.rs_price), it.wallet.roundToLong()),
                                Constants.FontNames.OPEN_SANS_BOLD)

                binding?.balanceTextview?.text = spannableStringBuilder
                adapter?.notifyDataSetChanged()
            }
        })

        viewModel?.paymentMethods?.observe(this, Observer { it ->
            if (it != null) {
                adapter?.notifyMethodsChanged(it)
            }
        })

        viewModel?.errorMessage?.observe(this, Observer { it ->
            val hasError = it == null
            binding?.withdrawErrorLayout?.visibility = if (hasError) View.GONE else View.VISIBLE
            binding?.withdrawError?.text = if (hasError) StringUtils.EMPTY else it
        })

        viewModel?.showConfirmationDialog?.observe(this, Observer { it ->
            if (it) {
                confirmationDialog?.showDialog()
            } else
                confirmationDialog?.dismiss()
        })

        viewModel?.onWithdrawCompleted?.observe(this, Observer { it ->
            if (it) {
                ActivityStackManager.getInstance().startWithDrawCompleteActivity(this@WithdrawalActivity)
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        viewModel?.loadUserProfile()
    }

    /**
     * **initUi** is responsible to apply and
     * initialize events (related with views) to UI components
     */
    private fun initUi() {
        binding?.balanceEdittext?.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                Utils.hideKeyboard(this@WithdrawalActivity)
            }
            false
        }

        binding?.withdrawalSubmitLayout?.setOnClickListener {
            viewModel?.onSubmitClicked(binding?.balanceEdittext?.text?.toString() ?: StringUtils.EMPTY)
        }

        binding?.balanceEdittext?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                val hasText = !TextUtils.isEmpty(binding?.balanceEdittext?.text?.toString())
                if (hasText) {
                    binding?.withdrawalSubmitLayout?.setBackgroundColor(
                            ContextCompat.getColor(this@WithdrawalActivity, R.color.colorAccent)
                    )
                } else {
                    binding?.withdrawalSubmitLayout?.setBackgroundColor(
                            ContextCompat.getColor(this@WithdrawalActivity, R.color.color_A7A7A7)
                    )
                    viewModel?.removeWarnings()
                }
                binding?.withdrawalSubmitLayout?.isEnabled = hasText
                binding?.withdrawalSubmitLayout?.isClickable = hasText
            }
        })
    }

    /**
     * Setting up payment's recyclerview
     */
    private fun setupRecyclerView() {
        adapter = WithdrawalPaymentMethodsAdapter(ArrayList<WithdrawPaymentMethod>(0),
                viewModel)
        binding?.paymentsRecyclerView?.adapter = adapter
    }

    /**
     * Removing activity from stack, this method is calling from view's onclick event
     *
     * @param v back button
     */
    fun finishActivity(v: View) {
        finish()
    }

    companion object {

        /**
         * Request code from which this activity is opening,
         * This is used to share data back to the last activity available in stack
         */
        const val REQ_CODE_WITH_DRAW = 12

        /**
         * This method is used to open withdrawal activity by using intent API mentioned by android docs.
         * For more info on intents, refers the below URL,
         *
         * @param activity context to open withdrawal activity
         * @see [Intents](https://developer.android.com/reference/android/content/Intent)
         */
        fun openActivity(activity: Activity) {
            val i = Intent(activity, WithdrawalActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivityForResult(i, REQ_CODE_WITH_DRAW)
        }
    }

    /**
     * This method is binded with cardview to open keyboard on click
     *
     * @param v on which user taps
     */
    fun onCardClick(v: View) {
        val imm = this@WithdrawalActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding?.balanceEdittext, SHOW_IMPLICIT)
    }
}