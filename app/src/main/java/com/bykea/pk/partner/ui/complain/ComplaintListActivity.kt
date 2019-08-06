package com.bykea.pk.partner.ui.complain

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainListBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.LastAdapter
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.ui.helpers.FontUtils
import com.bykea.pk.partner.utils.Dialogs
import kotlinx.android.synthetic.main.activity_problem.*
import org.apache.commons.lang3.StringUtils
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

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayShowTitleEnabled(false)


        toolbar_title.text = SpannableStringBuilder("")
                .append(StringUtils.SPACE)
                .append(FontUtils.getStyledTitle(this@ComplaintListActivity, getString(R.string.title_new_complain_ur), "jameel_noori_nastaleeq.ttf"))
                .append(FontUtils.getStyledTitle(this@ComplaintListActivity, StringUtils.SPACE, "roboto_medium.ttf"))
                .append(FontUtils.getStyledTitle(this@ComplaintListActivity, getString(R.string.title_new_complain_en), "roboto_medium.ttf"))
                .append(StringUtils.SPACE)


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
}
