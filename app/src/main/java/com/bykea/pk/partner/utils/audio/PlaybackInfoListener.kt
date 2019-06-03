package com.yousufsohail.myaudioplayer

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

    internal open fun onDurationChanged(duration: Int) {}

    internal open fun onPositionChanged(position: Int) {}

    internal open fun onStateChanged(@State state: Int) {}

    internal open fun onPlaybackCompleted() {}

}