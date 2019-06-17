package com.bykea.pk.partner.ui.loadboard.list

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.LoadboardBookingsFragBinding
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import com.bykea.pk.partner.ui.loadboard.common.setupSnackbar
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.loadboard_bookings_frag.*


/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    BookingListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 */
class LoadBoardListFragment : Fragment() {

    private lateinit var viewDataBinding: LoadboardBookingsFragBinding
    private lateinit var listAdapter: BookingsAdapter
    private var mBehavior: BottomSheetBehavior<*>? = null

    var layoutParamRLZero = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    var layoutParamRL: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutParamRLZero.setMargins(0, 0, 0, 0);
        layoutParamRL.setMargins(0, resources.getDimension(R.dimen._minus8sdp).toInt(), 0, 0);

        viewDataBinding = LoadboardBookingsFragBinding.inflate(inflater, container, false).apply {

            viewmodel = obtainViewModel(BookingListViewModel::class.java).apply {
                openBookingEvent.observe(this@LoadBoardListFragment, Observer {
                    if (mBehavior != null && mBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        mBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        ActivityStackManager.getInstance().startLoadboardBookingDetailActiivty(activity, it.peekContent())
                    }
                })

                dataLoading.observe(this@LoadBoardListFragment, Observer {
                    if (it) Dialogs.INSTANCE.showLoader(activity)
                    else Dialogs.INSTANCE.dismissDialog()
                })
                isExpended.observe(this@LoadBoardListFragment, Observer {
                    if (it) {
                        relativeLayoutBottomView.visibility = View.VISIBLE
                        relativeLayoutBottomSheet.setLayoutParams(layoutParamRLZero);
                    } else {
                        relativeLayoutBottomView.visibility = View.GONE
                        if (viewmodel?.empty?.value!!) {
                            relativeLayoutBottomSheet.setLayoutParams(layoutParamRLZero);
                        } else {
                            relativeLayoutBottomSheet.setLayoutParams(layoutParamRL);
                        }
                    }
                })

                empty.observe(this@LoadBoardListFragment, Observer {
                    if (it) {
                        relativeLayoutBottomSheet.setLayoutParams(layoutParamRLZero);
                    } else {
                        if (!viewmodel?.isExpended?.value!!)
                            relativeLayoutBottomSheet.setLayoutParams(layoutParamRL);
                    }
                })


            }

            listener = object : BookingsListUserActionsListener {
                override fun onBackClicked() {
                    mBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                override fun onRefreshClicked() {
                    viewDataBinding.viewmodel!!.refresh()
                }
            }
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.post {
            val parent = view.getParent() as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            params.height = Resources.getSystem().displayMetrics.heightPixels
            val behavior = params.behavior
            mBehavior = behavior as BottomSheetBehavior<*>?
            mBehavior!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            viewDataBinding.bookingsList.smoothScrollToPosition(0)
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    toggleBottomSheetToolbar(slideOffset)
                }
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
        if (mBehavior != null &&
                mBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            viewDataBinding.viewmodel!!.isExpended.value = true
        }
    }

    /**
     * Setup List Adapter
     *
     */
    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            ViewCompat.setNestedScrollingEnabled(viewDataBinding.bookingsList, true)
            listAdapter = BookingsAdapter(java.util.ArrayList(0), viewModel)
            viewDataBinding.bookingsList.adapter = listAdapter
        } else {
            Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    /**
     * Toggle Bottom Sheet Toolbar
     *
     * @param alpha Alpha value to be applied on Toolbar
     */
    private fun toggleBottomSheetToolbar(alpha: Float) {
        if (alpha > Constants.BOTTOM_SHEET_ALPHA_VALUE) {
            bottomSheetToolbarLayout.visibility = View.VISIBLE
            bottomSheetToolbarDivider.visibility = View.VISIBLE
            bottomSheetPickDropLayout.visibility = View.VISIBLE
            bottomSheetPickDropDivider.visibility = View.VISIBLE
            bottomSheetToolbarLayout.alpha = alpha
            bottomSheetToolbarDivider.alpha = alpha
            bottomSheetPickDropLayout.alpha = alpha
            bottomSheetPickDropDivider.alpha = alpha
            viewDataBinding.viewmodel!!.isExpended.value = true
        } else {
            bottomSheetToolbarLayout.visibility = View.GONE
            bottomSheetToolbarDivider.visibility = View.GONE
            bottomSheetPickDropLayout.visibility = View.GONE
            bottomSheetPickDropDivider.visibility = View.GONE
            bottomSheetToolbarLayout.alpha = alpha
            bottomSheetToolbarDivider.alpha = alpha
            bottomSheetPickDropLayout.alpha = alpha
            bottomSheetPickDropDivider.alpha = alpha
            viewDataBinding.viewmodel!!.isExpended.value = false
        }
    }

    companion object {
        fun newInstance() = LoadBoardListFragment()
        private const val TAG = "BookingsFragment"
    }
}
