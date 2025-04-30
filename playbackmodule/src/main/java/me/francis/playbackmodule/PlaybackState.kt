package me.francis.playbackmodule

data class PlaybackState(
    val isPlaying: Boolean = false,
    val isReady: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0
)