package com.bykea.pk.partner.ui.nodataentry

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.databinding.ActivityViewDeliveryDetailsBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.obtainViewModel
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.*
import com.bykea.pk.partner.utils.Constants.*
import com.bykea.pk.partner.utils.audio.BykeaAmazonClient
import com.bykea.pk.partner.utils.audio.Callback
import com.zendesk.util.StringUtils
import kotlinx.android.synthetic.main.activity_view_delivery_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.io.File
import java.io.FileInputStream

class ViewDeliveryDetailsActivity : BaseActivity() {
    private var TAG = ViewDeliveryDetailViewsModel::class.java.simpleName
    lateinit var binding: ActivityViewDeliveryDetailsBinding
    lateinit var viewModel: ViewDeliveryDetailViewsModel
    private var mediaPlayer: MediaPlayer? = null
    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_delivery_details)
        viewModel = obtainViewModel(ViewDeliveryDetailViewsModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (intent?.extras!!.containsKey(Constants.Extras.DELIVERY_DETAILS_OBJECT)) {
            viewModel.deliveryDetails.value = intent?.extras!!.getParcelable(Constants.Extras.DELIVERY_DETAILS_OBJECT) as DeliveryDetails
        }

        viewModel.getActiveTrip()
        binding.listener = object : GenericListener {
            override fun navigateToPlaceSearch() {
                Util.safeLet(viewModel.deliveryDetails.value,
                        viewModel.deliveryDetails.value?.dropoff,
                        viewModel.deliveryDetails.value?.dropoff?.lat,
                        viewModel.deliveryDetails.value?.dropoff?.lng) { _, _, lat, lng ->
                    Utils.navigateToGoogleMap(this@ViewDeliveryDetailsActivity,
                            AppPreferences.getLatitude(), AppPreferences.getLongitude(), lat, lng)
                }
            }

            override fun onPlayAudio(url: String?) {
                if (url != null) {
                    voiceClipPlayDownload(url)
                } else {
                    Dialogs.INSTANCE.showToast(getString(R.string.no_voice_note_available))
                }
            }

            override fun onStopAudio() {
                if (mediaPlayer != null) {
                    imgViewAudioPlay.setImageDrawable(ContextCompat.getDrawable(DriverApp.getContext(), R.drawable.ic_audio_play))
                    imgViewAudioPlay.isEnabled = true
                    progressBarForAudioPlay.visibility = View.GONE
                    mediaPlayer?.pause()
                }
            }

            override fun showCallDialog() {
                Dialogs.INSTANCE.showCallPassengerDialog(this@ViewDeliveryDetailsActivity, {
                    if (Utils.isAppInstalledWithPackageName(this@ViewDeliveryDetailsActivity, ApplicationsPackageName.WHATSAPP_PACKAGE)) {
                        Utils.openCallDialog(this@ViewDeliveryDetailsActivity, viewModel.callData.value, getSenderNumber())
                    } else {
                        Utils.callingIntent(this@ViewDeliveryDetailsActivity, getSenderNumber())
                    }
                    Utils.redLog(TAG, getString(R.string.call_sender))
                }, {
                    if (Utils.isAppInstalledWithPackageName(this@ViewDeliveryDetailsActivity, ApplicationsPackageName.WHATSAPP_PACKAGE)) {
                        Utils.openCallDialog(this@ViewDeliveryDetailsActivity, viewModel.callData.value, getRecipientNumber())
                    } else {
                        Utils.callingIntent(this@ViewDeliveryDetailsActivity, getRecipientNumber())
                    }
                    Utils.redLog(TAG, getString(R.string.call_recipent))
                })
            }
        }

        setTitleCustomToolbarWithUrdu(viewModel.deliveryDetails.value?.details?.trip_no, StringUtils.EMPTY_STRING)
        fLLocation.visibility = View.VISIBLE
        tVLocationAlphabet.text = viewModel.deliveryDetails.value?.details?.display_tag
    }

    /**
     * Download audio resource via Amazon SDK
     *
     * @param url Url to download from
     */
    private fun voiceClipPlayDownload(url: String) {
        if (mediaPlayer != null) {
            imgViewAudioPlay.setImageDrawable(ContextCompat.getDrawable(DriverApp.getContext(), R.drawable.ic_audio_stop))
            imgViewAudioPlay.isEnabled = false
            progressBarForAudioPlay.visibility = View.VISIBLE

            mediaPlayer?.start()
            startPlayProgressUpdater()
        } else {
            Dialogs.INSTANCE.showLoader(this@ViewDeliveryDetailsActivity)
            AppPreferences.getDriverSettings()?.data?.s3BucketVoiceNotes?.let {
                BykeaAmazonClient.getFileObject(url, object : Callback<File> {
                    override fun success(obj: File) {
                        Dialogs.INSTANCE.dismissDialog()

                        imgViewAudioPlay.setImageDrawable(ContextCompat.getDrawable(DriverApp.getContext(), R.drawable.ic_audio_stop))
                        imgViewAudioPlay.isEnabled = false
                        progressBarForAudioPlay.visibility = View.VISIBLE

                        mediaPlayer = MediaPlayer()
                        mediaPlayer?.setDataSource(FileInputStream(obj).fd)
                        mediaPlayer?.prepare()
                        progressBarForAudioPlay.max = mediaPlayer?.duration!!
                        mediaPlayer?.start()
                        startPlayProgressUpdater()
                    }

                    override fun fail(errorCode: Int, errorMsg: String) {
                        Dialogs.INSTANCE.showToast(DriverApp.getContext().getString(R.string.no_voice_note_available))
                        Dialogs.INSTANCE.dismissDialog()
                    }
                }, it)
            } ?: run {
                Dialogs.INSTANCE.dismissDialog()
                Utils.appToast(getString(R.string.settings_are_not_updated))
            }
        }
    }

    /**
     * Handling for the media player play and pause,
     * resume from the previous stopped position, and
     * set icon accordingly
     */
    fun startPlayProgressUpdater() {
        progressBarForAudioPlay.progress = mediaPlayer?.currentPosition!!
        if (mediaPlayer?.isPlaying!!) {
            val notification = Runnable {
                startPlayProgressUpdater()
            }
            handler.postDelayed(notification, DIGIT_THOUSAND.toLong())
        } else {
            mediaPlayer?.pause()

            imgViewAudioPlay.setImageDrawable(ContextCompat.getDrawable(DriverApp.getContext(), R.drawable.ic_audio_play))
            imgViewAudioPlay.isEnabled = true
            progressBarForAudioPlay.visibility = View.GONE
            progressBarForAudioPlay.progress = DIGIT_ZERO
        }
    }

    /***
     * Get Sender phone number according
     * @return Phone number for Sender
     */
    private fun getSenderNumber(): String {
        viewModel.callData.value?.let {
            return if (!it.phoneNo.isNullOrEmpty() && !it.senderPhone.isNullOrEmpty()) {
                var passengerPhoneNumber = it.phoneNo
                var senderPhoneNumber = it.senderPhone
                if (passengerPhoneNumber.startsWith(MOBILE_COUNTRY_STANDARD))
                    passengerPhoneNumber = Utils.phoneNumberToShow(it.phoneNo)
                if (senderPhoneNumber.startsWith(MOBILE_COUNTRY_STANDARD))
                    senderPhoneNumber = Utils.phoneNumberToShow(it.senderPhone)

                if (passengerPhoneNumber.equals(senderPhoneNumber, ignoreCase = true))
                    passengerPhoneNumber
                else
                    senderPhoneNumber
            } else if (!it.phoneNo.isNullOrEmpty()) {
                it.phoneNo
            } else {
                return StringUtils.EMPTY_STRING
            }
        }
        return StringUtils.EMPTY_STRING
    }

    /***
     * Get Sender phone number according
     * @return Phone number for Sender
     */
    private fun getRecipientNumber(): String {
        if (!viewModel.deliveryDetails.value?.dropoff?.phone.isNullOrEmpty()) {
            return viewModel.deliveryDetails.value?.dropoff?.phone.toString()
        }
        return StringUtils.EMPTY_STRING
    }

    override fun onPause() {
        if (mediaPlayer != null) {
            mediaPlayer?.pause()
            startPlayProgressUpdater()
        }
        super.onPause()
    }
}
