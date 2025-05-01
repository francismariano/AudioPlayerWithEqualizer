package me.francis.playbackmodule

import android.net.Uri

data class PlaybackState(
    val isPlaying: Boolean = false,
    val isReady: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0,
    val currentTrackIndex: Int = -1,
    val playlistSize: Int = 0,
    val currentTrack: Uri? = null
)