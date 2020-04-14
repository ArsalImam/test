package com.bykea.pk.partner.ui.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.data.BookingDetail
import com.bykea.pk.partner.dal.source.remote.data.Invoice
import com.bykea.pk.partner.dal.source.remote.response.BookingDetailResponse
import com.bykea.pk.partner.dal.util.BOOKING_CURRENT_DATE_FORMAT
import com.bykea.pk.partner.dal.util.BOOKING_ID_TO_REPLACE
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Utils
import com.zendesk.util.StringUtils
import java.text.SimpleDateFormat

/**
 * This is the view model class for [BookingDetailActivity]
 *
 * @author Arsal Imam
 */
class BookingDetailViewModel
/**
 * Constructor of this viewModel
 * @param [jobsRepository] instance to get and update data
 */
(private val jobsRepository: JobsRepository) : ViewModel() {

    /**
     * observable to show/hide loader
     */
    private val _bookingDetailData = MutableLiveData<BookingDetail>().apply { value = null }
    val bookingDetailData: LiveData<BookingDetail>
        get() = _bookingDetailData

    /**
     * tis observer is responsible to show/hide complain button
     */
    private val _showComplainButton = MutableLiveData<Boolean>().apply { value = true }
    val showComplainButton: LiveData<Boolean>
        get() = _showComplainButton

    /**
     * observable to show/hide loader
     */
    private val _showLoader = MutableLiveData<Boolean>().apply { value = false }
    val showLoader: LiveData<Boolean>
        get() = _showLoader

    /**
     * this method will hit server to get/update [bookingDetailData] by id
     * @param bookingId id of the booking
     */
    fun updateBookingDetailById(bookingId: String) {
        _showLoader.value = true

        val url = AppPreferences.getSettings().settings.bookingDetailByIdUrl
                .replace(BOOKING_ID_TO_REPLACE, bookingId)

        jobsRepository.getBookingDetailsById(url, object : JobsDataSource.GetBookingDetailCallback {
            override fun onSuccess(bookingDetailResponse: BookingDetailResponse) {
                _showLoader.value = false
                updateBookingDetailObject(bookingDetailResponse.data)
            }

            override fun onFail(code: Int, message: String?) {
                super.onFail(code, message)
                _showLoader.value = false
                Utils.appToast(message)
            }
        })
    }

    /**
     * this method will update UI on [bookingDetailData] gets updated
     * @param data newly received data
     */
    private fun updateBookingDetailObject(data: BookingDetail) {
        _showComplainButton.value = Utils.getDaysInBetween(System.currentTimeMillis(),
                SimpleDateFormat(BOOKING_CURRENT_DATE_FORMAT).parse(data.dt).time) <=
                AppPreferences.getSettings().settings.trip_support_max_days

        if (data.rate?.partner != null) {
            data.invoice?.add(Invoice(false, Constants.SEPERATOR_ABOVE,
                    StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING,
                    DriverApp.getContext().getString(R.string.partner_ki_taraf_se_rating),
                    StringUtils.EMPTY_STRING, null, false, Constants.BOOKING_DETAIL_VIEW_TYPE_RATING, data.rate?.customer?.toFloat()!!))

        }

        if (data.rate?.customer != null) {
            data.invoice?.add(Invoice(false, Constants.SEPERATOR_ABOVE,
                    StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING,
                    DriverApp.getContext().getString(R.string.customer_ki_taraf_se_rating),
                    StringUtils.EMPTY_STRING, null, false, Constants.BOOKING_DETAIL_VIEW_TYPE_RATING, data.rate?.partner?.toFloat()!!))
        }

        _bookingDetailData.value = data
    }
}