package com.bykea.pk.partner.ui.loadboard.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.databinding.JobListItemBinding

/**
 * List Adapter for [Job] listing to be shown on Loadboard
 *
 * @property jobs List of booking
 * @property bookingsViewModel ViewModel for [JobListFragment]
 */
class JobListAdapter(
        private var jobs: List<Job>,
        private val bookingsViewModel: JobListViewModel
) : BaseAdapter() {

    fun replaceData(jobs: List<Job>) {
        setList(jobs)
    }

    override fun getCount() = jobs.size

    override fun getItem(position: Int) = jobs[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: JobListItemBinding
        binding = if (view == null) {
            // Inflate
            val inflater = LayoutInflater.from(viewGroup.context)

            // Create the binding
            JobListItemBinding.inflate(inflater, viewGroup, false)
        } else {
            // Recycling view
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        val userActionsListener = object : JobListItemActionsListener {

            override fun onBookingClicked(job: Job) {
                bookingsViewModel.openBooking(job.id)
            }
        }

        with(binding) {
            booking = jobs[position]
            listener = userActionsListener
            executePendingBindings()
        }

        return binding.root
    }


    private fun setList(jobs: List<Job>) {
        this.jobs = jobs
        notifyDataSetChanged()
    }
}
