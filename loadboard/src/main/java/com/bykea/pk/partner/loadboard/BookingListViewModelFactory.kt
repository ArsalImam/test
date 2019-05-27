package com.bykea.pk.partner.loadboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating a [BookingListViewModel] with a constructor that takes a [BookingRepository].
 *
 * @Author: Yousuf Sohail
 */
class BookingListViewModelFactory(private val repository: BookingRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = BookingListViewModel(repository) as T
}
