package com.bykea.pk.partner.utils.audio

import android.widget.ProgressBar
import android.widget.SeekBar

class PlayBackListener(private val mProgressBarAudio: ProgressBar, private val mUserIsSeeking: Boolean) : PlaybackInfoListener() {

    override fun onDurationChanged(duration: Int) {
        mProgressBarAudio.max = duration
    }

    override fun onPositionChanged(position: Int) {
        if (!mUserIsSeeking) {
            mProgressBarAudio.progress = position
        }
    }

    override fun onStateChanged(@State state: Int) {

    }

}
