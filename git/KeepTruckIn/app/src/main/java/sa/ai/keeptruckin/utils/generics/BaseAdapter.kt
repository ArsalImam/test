package sa.ai.keeptruckin.utils.generics

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 */

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import sa.ai.keeptruckin.BR

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * this is a generic implementation for [RecyclerView.Adapter] & [RecyclerView.ViewHolder],
 * and can be used to load data with minimal code changes
 *
 * [layout] resource id from which the cell needs to be created
 * [itemClickListener] callback, will trigger when user taps on any of the cell
 */
open class BaseAdapter<T> internal constructor(
    @LayoutRes private val layout: Int,
    private val itemClickListener: OnItemClickListener<T>
) : RecyclerView.Adapter<BaseAdapter<T>.MyViewHolder>() {

    /**
     * list of items, showing in recycler view
     */
    var items: List<T> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * interface callback, will trigger when user taps on any of the cell
     */
    interface OnItemClickListener<T> {
        /**
         * callback, will trigger when user taps on any of the cell
         * [item] on which user tapped
         */
        fun onItemClick(item: T)
    }

    /**
     * {@inheritDoc}
     *
     * This will calls on every new initialization of the cell,
     * It can be used for any initializations/bindings or on start executions
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return MyViewHolder(binding)
    }

    /**
     * {@inheritDoc}
     *
     * This will calls every time when recycler view performs binding,
     * useful to bind data with view
     *
     * [holder] view presentation of the cell
     * [position] index of the item recycled
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickListener.onItemClick(item) }
        holder.bind(item)
    }

    /**
     * {@inheritDoc}
     *
     * will return total count of the items, available in list
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * {@inheritDoc}
     *
     * this method will return type of cell to be rendered on the position,
     * [position] index of the item recycled
     */
    override fun getItemViewType(position: Int): Int {
        return layout
    }

    /**
     * view presentation of the cell
     */
    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * This will calls every time when recycler view performs binding,
         * useful to bind data with view

         * [item] object is the tapped object
         */
        fun bind(item: T) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }
    }
}
