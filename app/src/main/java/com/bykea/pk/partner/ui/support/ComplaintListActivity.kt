package com.bykea.pk.partner.ui.support

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainListBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.loadboard.common.LastAdapter
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import com.bykea.pk.partner.utils.Dialogs
import kotlinx.android.synthetic.main.activity_problem.*
import zendesk.commonui.UiConfig
import zendesk.support.Request
import zendesk.support.request.RequestActivity


/**
 * An activity representing a list of Complaints.
 *
 * @author: Yousuf Sohail
 */
class ComplaintListActivity : BaseActivity() {

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
                            RequestActivity.builder().withRequest(item).show(this@ComplaintListActivity)
                        }
                    })
            if (AppPreferences.isEmailVerified()) {
                Dialogs.INSTANCE.showLoader(this@ComplaintListActivity)
                this.start()
            }
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
