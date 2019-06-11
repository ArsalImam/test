package com.bykea.pk.partner.ui.loadboard.list

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.databinding.LoadboardBookingsFragBinding
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import com.bykea.pk.partner.ui.loadboard.common.setupSnackbar
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Utils
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
 * You activity (or fragment) needs to implement [LoadBoardListFragment.onLoadBoardListFragmentInteractionListener].
 */
class LoadBoardListFragment : Fragment() {

    private lateinit var viewDataBinding: LoadboardBookingsFragBinding
    private lateinit var listAdapter: BookingsAdapter

    private val isVisibleFirstTime = true

    private var mOnLoadBoardListFragmentInteractionListener: onLoadBoardListFragmentInteractionListener? = null
    public var mBehavior: BottomSheetBehavior<*>? = null
    private var bookingArrayList: ArrayList<Booking>? = ArrayList()

    var layoutParamRLZero = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    var layoutParamRL: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewDataBinding = LoadboardBookingsFragBinding.inflate(inflater, container, false).apply {
            viewmodel = obtainViewModel(BookingListViewModel::class.java).apply {
                openBookingEvent.observe(this@LoadBoardListFragment, Observer {
                    if (mBehavior != null && mBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        mBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        Utils.appToast(activity, "$it")

                        ActivityStackManager.getInstance().startLoadboardBookingDetailActiivty(activity, it.peekContent())
                    }
                })
            }
        }
        return viewDataBinding.root
    }

    override fun onStart() {
        super.onStart()
        mBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setupListAdapter()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = BookingsAdapter(java.util.ArrayList(0), viewModel)
            viewDataBinding.bookingsList.adapter = listAdapter
        } else {
            Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutParamRLZero.setMargins(0, 0, 0, 0);
        layoutParamRL.setMargins(0, -15, 0, 0);

//        getData()

//        setAdapter(bookingArrayList)
        //bsetBottomSheetArrow()

        view.post {
            val parent = view.getParent() as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            params.height = getScreenHeight()
            val behavior = params.behavior
            mBehavior = behavior as BottomSheetBehavior<*>?
            mBehavior!!.peekHeight = 100
            mBehavior!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
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

            bottomSheetRefreshIV.setOnClickListener {
                //refreshLoadBoardListingAPI();
            }

            bottomSheetNoJobsAvailableTV.setOnClickListener {
                if (mBehavior != null && mBehavior?.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                }
            }
        }
    }

/*    private fun getData() {
        val call = ApiClient.build()?.getLoadboardListMock()
        call?.enqueue(object : Callback<GetLoadboardListingResponse> {

            override fun onFailure(call: Call<GetLoadboardListingResponse>, t: Throwable) {
//                callback.onDataNotAvailable(t.message)
                Toast.makeText(this@LoadBoardListFragment.activity, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<GetLoadboardListingResponse>, response: Response<GetLoadboardListingResponse>) {
                response.body()?.let {
                    if (response.isSuccessful && it.isSuccess()) {
                        Log.v(BookingsRemoteDataSource::class.java.simpleName, "data ${it.data}")
//                        setAdapter(it.data)
                    } else {
                        Toast.makeText(this@LoadBoardListFragment.activity, "Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }*/

/*
    private fun setAdapter(bookingArrayList: List<Booking>?) {
        if (true) {//bookingArrayList != null && bookingArrayList.size > 0) {
            bookingsList.layoutManager = LinearLayoutManager(context)
            bookingsList.adapter = LoadBoardListAdapter(activity, ArrayList<Booking>(), LoadBoardListAdapter.ItemClickListener {
                if (mBehavior != null && mBehavior!!.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
//                      ActivityStackManager.getInstance().startLoadboardBookingDetailActiivty(mCurrentActivity, item.getId());
                }
                Utils.appToast(activity?.applicationContext, "Loadboard Booking Id: ${it}")
            })
        } else {
            showBottomSheetNoJobsAvailableHint()
        }
    }

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
            mBehavior!!.peekHeight = 100
            relativeLayoutBottomSheet.setLayoutParams(layoutParamRLZero);
            appBottomBarLayoutImgView.setVisibility(View.GONE);
        } else {
            bottomSheetToolbarLayout.visibility = View.GONE
            bottomSheetToolbarDivider.visibility = View.GONE
            bottomSheetPickDropLayout.visibility = View.GONE
            bottomSheetPickDropDivider.visibility = View.GONE
            bottomSheetToolbarLayout.alpha = alpha
            bottomSheetToolbarDivider.alpha = alpha
            bottomSheetPickDropLayout.alpha = alpha
            bottomSheetPickDropDivider.alpha = alpha

            setBottomSheetArrow()
        }
    }

    fun setBottomSheetArrow() {
        if (isVisibleFirstTime) {// && bookingArrayList != null && bookingArrayList!!.size > 0) {
            //  isVisibleFirstTime || bottomSheetLoader.getVisibility() == View.VISIBLE) {
            //  isVisibleFirstTime = false;
            appBottomBarLayoutImgView.setVisibility(View.VISIBLE);
            relativeLayoutBottomSheet.setLayoutParams(layoutParamRL);
            mBehavior!!.peekHeight = 130
        } else {
            mBehavior!!.peekHeight = 100
            relativeLayoutBottomSheet.setLayoutParams(layoutParamRLZero);
        }
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().getDisplayMetrics().heightPixels
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mOnLoadBoardListFragmentInteractionListener = parent as onLoadBoardListFragmentInteractionListener
        } else {
            mOnLoadBoardListFragmentInteractionListener = context as onLoadBoardListFragmentInteractionListener
        }
    }*/

    override fun onDetach() {
        mOnLoadBoardListFragmentInteractionListener = null
        super.onDetach()
    }

    /**
     * show progress loader while loadboard jobs listing api is being requested
     */
    private fun showBottomSheetLoader() {
        bottomSheetLoader.visibility = View.VISIBLE
        bottomSheetNoJobsAvailableTV.visibility = View.GONE
        bookingsList.visibility = View.GONE
    }

    /**
     * show No Jobs Available as hint to the user that selected zone does not have job yet.
     */
    private fun showBottomSheetNoJobsAvailableHint() {
        bottomSheetLoader.visibility = View.GONE
        bottomSheetNoJobsAvailableTV.visibility = View.VISIBLE
        bookingsList.visibility = View.GONE
    }

    interface onLoadBoardListFragmentInteractionListener {
        fun onBookingClicked(booking: Long)
    }

    /*private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_booking_list_dialog_item, parent, false)) {

        internal val li_LoadboardDropOffTV: AutoFitFontTextView = itemView.li_LoadboardDropOffTV
        internal val tvFare: AutoFitFontTextView = itemView.tv_fare

        init {
            itemView.setOnClickListener {
                mOnLoadBoardListFragmentInteractionListener?.onBookingClicked(getItem(adapterPosition))
            }
        }

        private fun getItem(adapterPosition: Int): Long {
            return adapterPosition.toLong() //TODO : REMOVE
//            return bookingArrayList!!.get(adapterPosition).id;
        }
    }

    private inner class BookingAdapter(val items: ArrayList<Booking>) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.li_LoadboardDropOffTV.setText(context!!.resources.getString(R.string.not_selected_ur))
            holder.tvFare.setText(context!!.resources.getString(R.string.dash))

            *//*val item: Booking = items.get(position)

            when (item.getTripType()) {
                Constants.TripTypes.RIDE_TYPE -> holder.ivServiceIcon.setImageResource(R.drawable.ride)
                Constants.TripTypes.PURCHASE_TYPE -> holder.ivServiceIcon.setImageResource(R.drawable.lay_ao)
                Constants.TripTypes.DELIVERY_TYPE -> holder.ivServiceIcon.setImageResource(R.drawable.bhejdo)
                else -> holder.ivServiceIcon.setImageResource(R.drawable.bhejdo)
            }
           if (item.getDropoffZone() != null) {
                holder.li_LoadboardDropOffTV.setText(context!!.resources.getString(R.string.pick_drop_name_ur, item.getDropoffZone().getUrduName()))
            } else {
                holder.li_LoadboardDropOffTV.setText(context!!.resources.getString(R.string.not_selected_ur))
            }
            if (item.getFare() != null) {
                holder.tvFare.setText(context!!.resources.getString(R.string.seleted_amount_rs, item.getFare()))
            } else {
                holder.tvFare.setText(context!!.resources.getString(R.string.dash))
            }*//*
        }

        override fun getItemCount(): Int {
            return 6
        }
    }*/

    companion object {
        fun newInstance() = LoadBoardListFragment()
        private const val TAG = "BookingsFragment"
    }
}
