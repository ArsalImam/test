package com.bykea.pk.partner.ui.loadboard.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.dal.JobRequest
import com.bykea.pk.partner.databinding.JobRequestListItemBinding

/**
 * List Adapter for [JobRequest] listing to be shown on Loadboard
 *
 * @property jobRequests List of booking
 * @property bookingsViewModel ViewModel for [JobRequestListFragment]
 */
class JobRequestListAdapter(
        private var jobRequests: List<JobRequest>,
        private val bookingsViewModel: JobRequestListViewModel
) : BaseAdapter() {

    fun replaceData(jobRequests: List<JobRequest>) {
        setList(jobRequests)
    }

    override fun getCount() = jobRequests.size

    override fun getItem(position: Int) = jobRequests[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: JobRequestListItemBinding
        binding = if (view == null) {
            // Inflate
            val inflater = LayoutInflater.from(viewGroup.context)

            // Create the binding
            JobRequestListItemBinding.inflate(inflater, viewGroup, false)
        } else {
            // Recycling view
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        val userActionsListener = object : JobRequestListItemActionsListener {

            override fun onBookingClicked(jobRequest: JobRequest) {
                bookingsViewModel.openBooking(jobRequest.id)
            }
        }

        with(binding) {
            booking = jobRequests[position]
            listener = userActionsListener
            executePendingBindings()
        }

        return binding.root
    }


    private fun setList(jobRequests: List<JobRequest>) {
        this.jobRequests = jobRequests
        notifyDataSetChanged()
    }
}
