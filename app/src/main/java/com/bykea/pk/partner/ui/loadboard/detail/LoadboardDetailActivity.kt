package com.bykea.pk.partner.ui.loadboard.detail

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.LoadboardDetailActBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.loadboard.common.obtainViewModel
import com.bykea.pk.partner.ui.loadboard.common.setupSnackbar
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.Utils
import com.bykea.pk.partner.utils.audio.MediaPlayerHolder
import com.bykea.pk.partner.utils.audio.PlaybackInfoListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_confirm_drop_off_address.*
import kotlinx.android.synthetic.main.loadboard_detail_act.*

/**
 * Loadboard booking detail screen ACTIVITY - opening from homeScreen's loadboard listing items
 */
class LoadboardDetailActivity : BaseActivity() {

    private lateinit var binding: LoadboardDetailActBinding
    private var bookingId: Long = 0

    private lateinit var mMediaPlayerHolder: MediaPlayerHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.loadboard_detail_act)

        binding.viewmodel = obtainViewModel(BookingDetailViewModel::class.java).apply {
            view?.setupSnackbar(this@LoadboardDetailActivity, this.snackbarMessage, Snackbar.LENGTH_LONG)

            dataLoading.observe(this@LoadboardDetailActivity, Observer {
                if (it) Dialogs.INSTANCE.showLoader(this@LoadboardDetailActivity)
                else Dialogs.INSTANCE.dismissDialog()
            })

            acceptBookingCommand.observe(this@LoadboardDetailActivity, Observer {
                ActivityStackManager.getInstance().startJobActivity(this@LoadboardDetailActivity)
            })
        }
        binding.listener = object : BookingDetailUserActionsListener {
            override fun onPlayAudio(url: String?) {
                if (url != null) {
                    mMediaPlayerHolder = MediaPlayerHolder(this@LoadboardDetailActivity)
                    mMediaPlayerHolder.setPlaybackInfoListener(PlaybackListener())
                    mMediaPlayerHolder.loadUri(url)
                    voiceClipPlayDownload()
                } else {
                    binding.viewmodel!!.showSnackbarMessage(R.string.no_voice_note_available)
                }
            }

            override fun onNavigateToMap(pickLat: Double, pickLng: Double, dropLat: Double, dropLng: Double) {
                Utils.navigateToGoogleMap(this@LoadboardDetailActivity, pickLat, pickLng, dropLat, dropLng)
            }

            override fun onAcceptBooking() {
                binding.viewmodel!!.accept()
            }
        }

        bookingId = intent.getLongExtra(EXTRA_BOOKING_ID, 0)
        binding.viewmodel!!.start(bookingId)
    }

    private fun voiceClipPlayDownload() {
        mMediaPlayerHolder.play()
        imgViewAudioPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_audio_stop))
        imgViewAudioPlay.isEnabled = false
        progressBarForAudioPlay.visibility = View.VISIBLE
    }


    internal inner class PlaybackListener : PlaybackInfoListener() {
        override fun onDurationChanged(duration: Int) {
            progressBarForAudioPlay.max = duration
        }

        override fun onPositionChanged(position: Int) {
            progressBarForAudioPlay.progress = position
        }

        override fun onStateChanged(state: Int) {}

        override fun onPlaybackCompleted() {

        }
    }

    companion object {
        const val EXTRA_BOOKING_ID = "EXTRA_BOOKING_ID"
    }


/*

    */
    /**
     * initialize views and objects related to this screen
     */
    /*

    private fun initViews() {

        mRepository!!.loadboardBookingDetail(mCurrentActivity, bookingId, object : UserDataHandler() {
            override fun onLoadboardBookingDetailResponse(response: LoadboardBookingDetailResponse) {
                Dialogs.INSTANCE.dismissDialog()
                tVEstimatedFare!!.text = "Rs." + response.data.amount + ""
                tVCODAmount!!.text = "Rs." + response.data.cartAmount + ""

                //bookingNoTV.setText(response.getData().getOrderNo());
                //                bookingTypeIV.setImageResource();
                supportFragmentManager.beginTransaction()
                        .replace(R.id.bookingDetailContainerFL, LoadboardDetailFragment.newInstance(response.data))
                        .commitAllowingStateLoss()
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                Dialogs.INSTANCE.dismissDialog()
                if (errorCode == HTTPStatus.UNAUTHORIZED) {
                    Utils.onUnauthorized(mCurrentActivity)
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage)
                }
            }
        })
    }

    */
    /**
     * initialize click listeners for this screen's button or widgets
     */
    /*

    private fun initListeners() {
        backBtn!!.setOnClickListener(this)
        imgViewDelivery!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backBtn -> finish()

            R.id.imgViewDelivery -> Utils.appToast(applicationContext, "imgViewDelivery")
        }
    }
*/


}
