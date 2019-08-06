package com.bykea.pk.partner.ui.common

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.ui.complain.ComplaintListViewModel
import com.bykea.pk.partner.ui.loadboard.detail.JobRequestDetailViewModel
import com.bykea.pk.partner.ui.loadboard.list.JobRequestListViewModel

/**
 * A creator is used to inject the product ID into the ViewModel
 *
 *
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
class ViewModelFactory private constructor(private val bookingsRepository: JobsRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(JobRequestListViewModel::class.java) -> JobRequestListViewModel(bookingsRepository)
                    isAssignableFrom(JobRequestDetailViewModel::class.java) -> JobRequestDetailViewModel(bookingsRepository)
                    isAssignableFrom(ComplaintListViewModel::class.java) -> ComplaintListViewModel()
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(
                            Injection.provideJobsRepository(application.applicationContext))
                            .also { INSTANCE = it }
                }


        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
