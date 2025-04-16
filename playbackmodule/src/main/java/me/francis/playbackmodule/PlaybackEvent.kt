package me.francis.playbackmodule

sealed class PlaybackEvent {
    data object PlaybackPrepared : PlaybackEvent()
    data object PlaybackStarted : PlaybackEvent()
    data object PlaybackPaused : PlaybackEvent()
    data object PlaybackStopped : PlaybackEvent()
    data object PlaybackCompleted : PlaybackEvent()
    data class Error(val message: String) : PlaybackEvent()
    data class TrackChanged(val trackName: String) : PlaybackEvent()
}