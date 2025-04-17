package me.francis.playbackmodule

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlaybackModule {
    // Controles básicos
    fun play()
    fun pause()
    fun stop()
    fun seekTo(positionMs: Int)
    fun setVolume(volume: Float)
    fun skipToNext(path: String)
    fun skipToPrevious(path: String)
    fun release()

    // Propriedades
    val currentPosition: StateFlow<Int>
    val duration: StateFlow<Int>
    val isPlaying: StateFlow<Boolean>
    val currentTrack: StateFlow<String?>
    val playbackEvents: Flow<PlaybackEvent>

    // Gerenciamento de dados
    fun setDataSource(path: String)
}