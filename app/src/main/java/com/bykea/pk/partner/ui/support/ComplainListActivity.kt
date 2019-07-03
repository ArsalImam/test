package com.bykea.pk.partner.ui.support

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bykea.pk.partner.R
import com.bykea.pk.partner.ui.support.dummy.DummyContent
import com.bykea.pk.partner.utils.Utils
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import kotlinx.android.synthetic.main.activity_complain_list.*
import zendesk.support.Request
import zendesk.support.Support

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ComplainDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ComplainListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complain_list)
        setSupportActionBar(toolbar)
        toolbar.title = title
        setupRecyclerView(complain_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, twoPane)
    }

    private fun getAllRequests() {

        val requestProvider = Support.INSTANCE.provider()!!.requestProvider()
        requestProvider.getAllRequests(object : ZendeskCallback<List<Request>>() {
            override fun onSuccess(requests: List<Request>) {
                Utils.appToastDebug(this@ComplainListActivity, "Zendesk(createRequest) - onSuccess")
            }

            override fun onError(errorResponse: ErrorResponse) {
                Utils.appToastDebug(this@ComplainListActivity, "Zendesk(createRequest) - onError")
            }
        })
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: ComplainListActivity,
                                        private val values: List<DummyContent.DummyItem>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                if (twoPane) {
                    val fragment = ComplainDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ComplainDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.complain_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, ComplainDetailActivity::class.java).apply {
                        putExtra(ComplainDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.complain_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
//            holder.idView.text = item.id
//            holder.contentView.text = item.content

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            val idView: TextView = view.id_text
//            val contentView: TextView = view.content
        }
    }
}
