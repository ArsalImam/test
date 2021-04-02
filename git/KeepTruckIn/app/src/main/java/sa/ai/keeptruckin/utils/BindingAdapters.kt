package sa.ai.keeptruckin.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import sa.ai.keeptruckin.utils.generics.BaseAdapter

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * binding adapters for xml bindings
 */
object BindingAdapters {

    /**
     * this can be used in xml xml attribute to set list in recycler view's adapter
     * [recyclerView] view to bind
     * [list] datasource for the adapter
     */
    @BindingAdapter("app:items")
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun setItems(recyclerView: RecyclerView, list: List<Any>?) {
        if (list == null) return
        recyclerView.adapter?.let {
            with(recyclerView.adapter as BaseAdapter<Any>) {
                items = list
            }
        }
    }
/*

    */
    /**
     * this can be used in xmlattribute to load image from network via url,
     * this will also transforms the loaded bitmap into circle
     *
     * [imageView] view to bind
     * [url] from which image needs to load
     *//*

    @BindingAdapter("app:circleImage")
    @JvmStatic
    fun loadCircleImage(imageView: ImageView, url: String) {
        if (StringUtils.isBlank(url)) return
        Picasso.get().load(url).transform(CropCircleTransformation()).placeholder(R.color.bg_grey)
            .into(imageView)
    }

    */
    /**
     * this can be used in xmlattribute to load image from network via url,
     *
     * [imageView] view to bind
     * [url] from which image needs to load
     *//*

    @BindingAdapter("app:imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String) {
        if (StringUtils.isBlank(url)) return
        Picasso.get().load(url).placeholder(R.color.bg_grey).into(imageView)
    }
*/

}