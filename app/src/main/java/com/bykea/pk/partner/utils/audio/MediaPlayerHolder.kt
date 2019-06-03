package com.yousufsohail.myaudioplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MediaPlayerHolder(context: Context) {

    private val mContext: Context
    private var mMediaPlayer: MediaPlayer? = null
    private var mResourceId: Int = 0
    private var mAudioUri: String? = null
    private var mPlaybackInfoListener: PlaybackInfoListener? = null
    private var mExecutor: ScheduledExecutorService? = null
    private var mSeekbarPositionUpdateTask: Runnable? = null

    val isPlaying: Boolean
        get() = if (mMediaPlayer != null) {
            mMediaPlayer!!.isPlaying
        } else false

    init {
        mContext = context.applicationContext
    }

    private fun initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer!!.setOnCompletionListener {
                stopUpdatingCallbackWithPosition(true)
                if (mPlaybackInfoListener != null) {
                    mPlaybackInfoListener!!.onStateChanged(PlaybackInfoListener.State.COMPLETED)
                    mPlaybackInfoListener!!.onPlaybackCompleted()
                }
            }
        }
    }

    fun setPlaybackInfoListener(listener: PlaybackInfoListener) {
        mPlaybackInfoListener = listener
    }

    fun loadMedia(resourceId: Int) {
        mResourceId = resourceId

        initializeMediaPlayer()

        val assetFileDescriptor = mContext.resources.openRawResourceFd(mResourceId)
        try {
            mMediaPlayer!!.setDataSource(assetFileDescriptor)
        } catch (e: Exception) {
        }

        try {
            mMediaPlayer!!.prepare()
        } catch (e: Exception) {
        }

        initializeProgressCallback()
    }

    fun loadUri(audioUri: String?) {
        mAudioUri = audioUri

        initializeMediaPlayer()

        val uri = Uri.parse(audioUri)
        try {
            mMediaPlayer!!.setDataSource(mContext, uri)
        } catch (e: Exception) {
        }

        try {
            mMediaPlayer!!.prepare()
        } catch (e: Exception) {
        }

        initializeProgressCallback()

    }

    fun release() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    fun play() {
        if (mMediaPlayer != null && !mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.start()
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener!!.onStateChanged(PlaybackInfoListener.State.PLAYING)
            }
            startUpdatingCallbackWithPosition()
        }
    }

    fun reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
            loadUri(mAudioUri)
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener!!.onStateChanged(PlaybackInfoListener.State.RESET)
            }
            stopUpdatingCallbackWithPosition(true)
        }
    }

    fun pause() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.pause()
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener!!.onStateChanged(PlaybackInfoListener.State.PAUSED)
            }
        }
    }

    fun seekTo(position: Int) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.seekTo(position)
        }
    }

    private fun startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor()
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = Runnable { updateProgressCallbackTask() }
        }
        mExecutor!!.scheduleAtFixedRate(
            mSeekbarPositionUpdateTask,
            0,
            PLAYBACK_POSITION_REFRESH_INTERVAL_MS.toLong(),
            TimeUnit.MILLISECONDS
        )
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private fun stopUpdatingCallbackWithPosition(resetUIPlaybackPosition: Boolean) {
        if (mExecutor != null) {
            mExecutor!!.shutdownNow()
            mExecutor = null
            mSeekbarPositionUpdateTask = null
            if (resetUIPlaybackPosition && mPlaybackInfoListener != null) {
                mPlaybackInfoListener!!.onPositionChanged(0)
            }
        }
    }

    private fun updateProgressCallbackTask() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            val currentPosition = mMediaPlayer!!.currentPosition
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener!!.onPositionChanged(currentPosition)
            }
        }
    }

    fun initializeProgressCallback() {
        val duration = mMediaPlayer!!.duration
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener!!.onDurationChanged(duration)
            mPlaybackInfoListener!!.onPositionChanged(0)
        }
    }

    companion object {

        val PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000
    }

}
