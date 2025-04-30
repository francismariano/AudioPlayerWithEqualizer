package me.francis.playbackmodule

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlaybackModule {
    fun play()
    fun pause()
    fun stop()
    fun seekTo(position: Int)
    fun skipToNext()
    fun skipToPrevious()
    fun setDataSource(uri: Uri)
}
