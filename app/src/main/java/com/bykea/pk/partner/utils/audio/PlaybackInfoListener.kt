package com.bykea.pk.partner.utils.audio

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

abstract class PlaybackInfoListener {

    @Retention(RetentionPolicy.SOURCE)
    internal annotation class State {
        companion object {
            const val INVALID = -1
            const val PLAYING = 0
            const val PAUSED = 1
            const val RESET = 2
            const val COMPLETED = 3
        }
    }

    open fun onDurationChanged(duration: Int) {}

    open fun onPositionChanged(position: Int) {}

    open fun onStateChanged(@State state: Int) {}

    open fun onPlaybackCompleted() {}

}