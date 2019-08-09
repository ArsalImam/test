package com.bykea.pk.partner.ui.withdraw

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod
import com.bykea.pk.partner.databinding.AdapterWithdrawMethdodsBinding
import java.util.*

/**
 * this method will provide the datasource to the
 * payment method recyclerview on {WithdrawalActivity}
 *
 * @param withdrawPaymentMethods provides the list of payment methods to the datasource
 * @param viewModel will provide the business logic to the datasource
 *
 * @author Arsal Imam
 */
class WithdrawalPaymentMethodsAdapter(private val withdrawPaymentMethods: ArrayList<WithdrawPaymentMethod>,
                                      private val viewModel: WithdrawalViewModel?) : RecyclerView.Adapter<WithdrawalPaymentMethodsAdapter.ViewHolder>() {

    /**
     * last selected position to maintain checkbox selection
     */
    private var lastSelectedPosition: Int = 0

    /**
     * constructor of the class
     */
    init {
        this.lastSelectedPosition = 0
    }

    /**
     * {@inheritDoc}
     *
     * This method will invoke by the system to create view for row
     *
     * @param parent in which view will create
     * @param viewType this will be managed from {@link #getItemViewType(position: Int):Int}
     *
     * @return viewHolder instance to create row
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: AdapterWithdrawMethdodsBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.adapter_withdraw_methdods, parent, false)
        return ViewHolder(binding, binding.root)
    }

    /**
     * this method will bind view with data
     *
     * @param holder this was created in the {@link #onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder}
     * @param position of the row
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateViewHolder(viewModel, withdrawPaymentMethods[position])
    }

    /**
     * return total counts of the row to be created
     *
     * @return count of rows
     */
    override fun getItemCount(): Int {
        return withdrawPaymentMethods.size
    }

    /**
     * this method can be used to update the datasource's data
     *
     * @param it updated data through which new rows needs to be created
     */
    fun notifyMethodsChanged(it: List<WithdrawPaymentMethod>) {
        withdrawPaymentMethods.clear()
        withdrawPaymentMethods.addAll(it)
        notifyDataSetChanged()
    }

    /**
     * viewHolder of the datasource which will hold views
     * @param v view that needs to be hold
     */
    inner class ViewHolder(
            val binder: AdapterWithdrawMethdodsBinding, v: View)
        : RecyclerView.ViewHolder(v), View.OnClickListener {

        /**
         * constructor of the class
         */
        init {
            v.setOnClickListener(this)
        }

        /**
         * click event when user click on the row
         *
         * @param view on which user tapped
         */
        override fun onClick(view: View) {
            val position = adapterPosition
            val `object` = withdrawPaymentMethods[position]
            if (!`object`.isSelected) {
                `object`.isSelected = true
                viewModel?.selectedPaymentMethod = `object`
                withdrawPaymentMethods[lastSelectedPosition].isSelected = false
                lastSelectedPosition = position
                notifyDataSetChanged()
            }
        }

        /**
         * this method will bind data with viewholder from {@link #onBindViewHolder(holder: ViewHolder, position: Int)}
         *
         * @param withdrawPaymentMethod object of the row
         * @param viewModel responsible viewModel
         */
        fun updateViewHolder(viewModel: WithdrawalViewModel?,
                             withdrawPaymentMethod: WithdrawPaymentMethod) {
            binder.nicValTextview.text = viewModel?.getDriverCnicNumber()
            binder.feesValTextview.text = withdrawPaymentMethod.description
            binder.checkView.visibility = if (withdrawPaymentMethod.isSelected)
                View.VISIBLE
            else
                View.INVISIBLE
        }
    }
}