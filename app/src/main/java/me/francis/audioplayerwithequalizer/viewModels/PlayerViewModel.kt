package me.francis.audioplayerwithequalizer.viewModels

import android.content.Intent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.francis.audioplayerwithequalizer.services.AudioService
import me.francis.playbackmodule.PlaybackEvent

class PlayerViewModel : ViewModel() {

    private val audioService = AudioService()

    // todo: reaproveitar para estados da aplicação (play/pause, etc)
    private val _currentPosition = MutableStateFlow<Int>(0)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow<Int>(0)
    val duration = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow<Boolean>(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow<String?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    private val _playbackEvents = MutableStateFlow<PlaybackEvent>(PlaybackEvent.PlaybackStopped)
    val playbackEvents = _playbackEvents.asStateFlow()

    init {
        audioService.onCreate()
    }

    fun startAudioService(intent: Intent, flags: Int, startId: Int) {
        audioService.onStartCommand(
            intent = intent,
            flags = flags,
            startId = startId
        )
    }

    fun playPause() {
        if (audioService.isPlaying.value) {
            audioService.pause()
        } else {
            audioService.play()
        }
    }

    fun stop() {
        audioService.stop()
    }

    fun seekTo(positionMs: Int) {
        audioService.seekTo(positionMs = positionMs)
    }

    fun setVolume(volume: Float) {
        audioService.setVolume(volume = volume)
    }

    fun skipToNext(path: String) {
        audioService.skipToNext(path = path)
    }

    fun skipToPrevious(path: String) {
        audioService.skipToPrevious(path = path)
    }

    fun setDataSource(path: String) {
        audioService.setDataSource(path = path)
    }
}
