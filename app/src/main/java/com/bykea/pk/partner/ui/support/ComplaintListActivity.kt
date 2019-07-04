package com.bykea.pk.partner.ui.support

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainListBinding
import com.bykea.pk.partner.ui.loadboard.common.LastAdapter
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import zendesk.support.Request

/**
 * An activity representing a list of Complaints.
 *
 * @author: Yousuf Sohail
 */
class ComplaintListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityComplainListBinding = DataBindingUtil.setContentView(this, R.layout.activity_complain_list)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = title
        binding.lifecycleOwner = this
        binding.viewmodel = obtainViewModel(ComplaintListViewModel::class.java).apply {
            binding.complainList.adapter = LastAdapter(R.layout.complain_list_content,
                    object : LastAdapter.OnItemClickListener<Request> {
                        override fun onItemClick(item: Request) {
                            // start detail here
                        }

                    })
            this.start()
        }
    }
}
