package com.bykea.pk.partner.ui.support

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainListBinding
import com.bykea.pk.partner.ui.loadboard.common.LastAdapter
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import kotlinx.android.synthetic.main.activity_problem.*
import zendesk.support.Request
import zendesk.support.request.RequestActivity

/**
 * An activity representing a list of Complaints.
 *
 * @author: Yousuf Sohail
 */
class ComplaintListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityComplainListBinding = DataBindingUtil.setContentView(this, R.layout.activity_complain_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.text = getString(R.string.title_new_complain)

        binding.lifecycleOwner = this
        binding.viewmodel = obtainViewModel(ComplaintListViewModel::class.java).apply {
            binding.complainList.adapter = LastAdapter(R.layout.complain_list_content,
                    object : LastAdapter.OnItemClickListener<Request> {
                        override fun onItemClick(item: Request) {

                            RequestActivity.builder()
                                    .withRequestId(item.requesterId.toString())
                                    .show(this@ComplaintListActivity);
                        }
                    })
            Dialogs.INSTANCE.showLoader(this@ComplaintListActivity)
            this.start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
