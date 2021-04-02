package sa.ai.keeptruckin.city.search

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import sa.ai.keeptruckin.R
import sa.ai.keeptruckin.data.beans.City
import sa.ai.keeptruckin.databinding.ActivitySearchCityBinding
import sa.ai.keeptruckin.utils.generics.BaseAdapter

/**
 * [author] by `Arsal Imam`
 * [created] on 5/17/2020
 *
 * this activity component can be used to show feed's listings
 */
@AndroidEntryPoint
class SearchCityActivity : AppCompatActivity(), BaseAdapter.OnItemClickListener<City> {

    private lateinit var binding: ActivitySearchCityBinding
    private val viewModel: SearchCityViewModel by viewModels()

    /**
     * {@inheritDoc}
     *
     *
     * This will calls on every new initialization of this activity,
     * It can be used for any initializations or on start executions
     *
     * [savedInstanceState] to get data on activity state changed
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivitySearchCityBinding>(
            this,
            R.layout.activity_search_city
        ).apply {
            lifecycleOwner = this@SearchCityActivity
            viewModel = this@SearchCityActivity.viewModel
            recyclerView.apply {
                adapter = BaseAdapter(R.layout.item_search_city, this@SearchCityActivity)
            }
        }
    }

    fun onSearch(v: View) {
        viewModel.searchCity(binding.searchQuery.text.toString())
    }

    /**
     * this method will invoke by the adapter, when any of the feed cell tapped
     *
     * [item] object is the tapped object
     */
    override fun onItemClick(item: City) {
    }
}