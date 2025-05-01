package me.francis.playbackmodule

import android.net.Uri

interface PlaybackModule {
    fun play()
    fun pause()
    fun stop()
    fun seekTo(position: Int)
    fun skipToNext()
    fun skipToPrevious()
    fun skipTo(index: Int)
    fun setDataSource(uri: Uri)
}
