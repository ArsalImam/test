package com.bykea.pk.partner.ui.withdraw

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod

import java.util.ArrayList

class WithdrawalPaymentMethodsAdapter(private val withdrawPaymentMethods: ArrayList<WithdrawPaymentMethod>,
                                      private val viewModel: WithdrawalViewModel) : RecyclerView.Adapter<WithdrawalPaymentMethodsAdapter.ViewHolder>() {
    private var lastSelectedPosition: Int = 0

    init {
        this.lastSelectedPosition = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.adapter_withdraw_methdods, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mCnicTextView.text = viewModel.driverCnicNumber
        holder.mFeesTextView.text = withdrawPaymentMethods[position].description
        holder.mCheckImageView.visibility = if (withdrawPaymentMethods[position].isSelected)
            View.VISIBLE
        else
            View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return withdrawPaymentMethods.size
    }

    fun notifyMethodsChanged(it: List<WithdrawPaymentMethod>) {
        withdrawPaymentMethods.clear()
        withdrawPaymentMethods.addAll(it)
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private val mCnicTextView: TextView
        private val mFeesTextView: TextView
        private val mCheckImageView: ImageView

        init {
            mCnicTextView = v.findViewById(R.id.nic_val_textview)
            mFeesTextView = v.findViewById(R.id.fees_val_textview)
            mCheckImageView = v.findViewById(R.id.checkView)
            v.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val `object` = withdrawPaymentMethods[position]
            if (!`object`.isSelected) {
                `object`.isSelected = true
                viewModel.selectedPaymentMethod = `object`
                withdrawPaymentMethods[lastSelectedPosition].isSelected = false
                lastSelectedPosition = position
                notifyDataSetChanged()
            }
        }
    }
}