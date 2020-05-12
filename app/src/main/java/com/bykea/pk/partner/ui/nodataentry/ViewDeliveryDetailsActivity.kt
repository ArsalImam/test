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
import com.bykea.pk.partner.utils.Constants.DIGIT_THOUSAND
import com.bykea.pk.partner.utils.Constants.DIGIT_ZERO
import com.bykea.pk.partner.utils.audio.BykeaAmazonClient
import com.bykea.pk.partner.utils.audio.Callback
import com.zendesk.util.StringUtils
import kotlinx.android.synthetic.main.activity_view_delivery_details.*
import kotlinx.android.synthetic.main.activity_view_delivery_details.imgViewAudioPlay
import kotlinx.android.synthetic.main.activity_view_delivery_details.progressBarForAudioPlay
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.job_detail_act.*
import java.io.File
import java.io.FileInputStream

class ViewDeliveryDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityViewDeliveryDetailsBinding
    private var mediaPlayer: MediaPlayer? = null;
    private val handler: Handler = Handler();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_delivery_details)
        binding.viewModel = obtainViewModel(ViewDeliveryDetailViewsModel::class.java)
        binding.lifecycleOwner = this

        if (intent?.extras!!.containsKey(Constants.Extras.DELIVERY_DETAILS_OBJECT)) {
            binding.viewModel?.deliveryDetails?.value = intent?.extras!!.getParcelable(Constants.Extras.DELIVERY_DETAILS_OBJECT) as DeliveryDetails
        }

        binding.viewModel?.getActiveTrip()
        binding.listener = object : GenericListener {
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
                    imgViewAudioPlay.isEnabled = true;
                    progressBarForAudioPlay.visibility = View.GONE;
                    mediaPlayer?.pause();
                }
            }
        }

        setTitleCustomToolbarWithUrdu(binding.viewModel?.deliveryDetails?.value?.details?.trip_no, StringUtils.EMPTY_STRING)
        fLLocation.visibility = View.VISIBLE
        tVLocationAlphabet.text = binding.viewModel?.deliveryDetails?.value?.details?.display_tag

        iVDirectionPickUp.setOnClickListener {
            Util.safeLet(binding.viewModel?.deliveryDetails?.value,
                    binding.viewModel?.deliveryDetails?.value?.dropoff,
                    binding.viewModel?.deliveryDetails?.value?.dropoff?.lat,
                    binding.viewModel?.deliveryDetails?.value?.dropoff?.lng) { _, _, lat, lng ->
                Utils.navigateToGoogleMap(this@ViewDeliveryDetailsActivity,
                        AppPreferences.getLatitude(), AppPreferences.getLongitude(), lat, lng)
            }
        }
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
            BykeaAmazonClient.getFileObject(url, object : Callback<File> {
                override fun success(obj: File) {
                    Dialogs.INSTANCE.dismissDialog()

                    imgViewAudioPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_audio_stop))
                    imgViewAudioPlay.isEnabled = false
                    progressBarForAudioPlay.visibility = View.VISIBLE

                    mediaPlayer = MediaPlayer()
                    mediaPlayer?.setDataSource(FileInputStream(obj).fd);
                    mediaPlayer?.prepare()
                    progressBarForAudioPlay.setMax(mediaPlayer?.duration!!);
                    mediaPlayer?.start()
                    startPlayProgressUpdater()
                }

                override fun fail(errorCode: Int, errorMsg: String) {
                    Dialogs.INSTANCE.showToast(DriverApp.getContext().getString(R.string.no_voice_note_available))
                    Dialogs.INSTANCE.dismissDialog()
                }
            })
        }
    }

    fun startPlayProgressUpdater() {
        progressBarForAudioPlay.progress = mediaPlayer?.currentPosition!!;
        if (mediaPlayer?.isPlaying!!) {
            val notification = Runnable {
                startPlayProgressUpdater();
            }
            handler.postDelayed(notification, DIGIT_THOUSAND.toLong())
        } else {
            mediaPlayer?.pause();

            imgViewAudioPlay.setImageDrawable(ContextCompat.getDrawable(DriverApp.getContext(), R.drawable.ic_audio_play))
            imgViewAudioPlay.isEnabled = true;
            progressBarForAudioPlay.visibility = View.GONE;
            progressBarForAudioPlay.progress = DIGIT_ZERO;
        }
    }

    override fun onPause() {
        if (mediaPlayer != null) {
            mediaPlayer?.pause();
            startPlayProgressUpdater();
        }
        super.onPause()
    }
}
