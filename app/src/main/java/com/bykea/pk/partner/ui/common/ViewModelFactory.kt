package com.bykea.pk.partner.ui.common

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.ui.booking.BookingDetailViewModel
import com.bykea.pk.partner.ui.bykeacash.BykeaCashFormViewModel
import com.bykea.pk.partner.ui.complain.ComplaintListViewModel
import com.bykea.pk.partner.ui.loadboard.detail.JobDetailViewModel
import com.bykea.pk.partner.ui.loadboard.list.JobListViewModel
import com.bykea.pk.partner.ui.nodataentry.AddEditDeliveryDetailsViewModel
import com.bykea.pk.partner.ui.nodataentry.ListDeliveryDetailsViewModel
import com.bykea.pk.partner.ui.nodataentry.ViewDeliveryDetailViewsModel
import com.bykea.pk.partner.ui.withdraw.WithdrawalViewModel

/**
 * A creator is used to inject the product ID into the ViewModel
 *
 *
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
class ViewModelFactory private constructor(private val bookingsRepository: JobsRepository,
                                           private val withdrawRepository: WithdrawRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(JobListViewModel::class.java) -> JobListViewModel(bookingsRepository)
                    isAssignableFrom(WithdrawalViewModel::class.java) -> WithdrawalViewModel(withdrawRepository)
                    isAssignableFrom(JobDetailViewModel::class.java) -> JobDetailViewModel(bookingsRepository)
                    isAssignableFrom(ComplaintListViewModel::class.java) -> ComplaintListViewModel()
                    isAssignableFrom(BykeaCashFormViewModel::class.java) -> BykeaCashFormViewModel(bookingsRepository)
                    isAssignableFrom(BookingDetailViewModel::class.java) -> BookingDetailViewModel(bookingsRepository)
                    isAssignableFrom(ListDeliveryDetailsViewModel::class.java) -> ListDeliveryDetailsViewModel()
                    isAssignableFrom(AddEditDeliveryDetailsViewModel::class.java) -> AddEditDeliveryDetailsViewModel()
                    isAssignableFrom(ViewDeliveryDetailViewsModel::class.java) -> ViewDeliveryDetailViewsModel()
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
                            Injection.provideJobsRepository(application.applicationContext),
                            Injection.provideWithdrawRepository(application.applicationContext))
                            .also { INSTANCE = it }
                }


        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
