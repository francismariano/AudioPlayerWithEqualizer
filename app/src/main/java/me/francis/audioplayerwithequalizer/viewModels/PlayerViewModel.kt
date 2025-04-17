package me.francis.audioplayerwithequalizer.viewModels

import android.content.Intent
import androidx.lifecycle.ViewModel
import me.francis.audioplayerwithequalizer.services.AudioService

class PlayerViewModel : ViewModel() {

    private var audioService: AudioService? = null

    val currentPosition = audioService?.currentPosition?.value
    val currentTrack = audioService?.currentTrack?.value
    val duration = audioService?.duration?.value
    val isPlaying = audioService?.isPlaying?.value
    val playbackEvents = audioService?.playbackEvents

    init {
        audioService?.onCreate()
    }

    fun setAudioService(service: AudioService) {
        audioService = service
    }

    fun startAudioService(intent: Intent, flags: Int, startId: Int) {
        audioService?.onStartCommand(
            intent = intent,
            flags = flags,
            startId = startId
        )
    }

    fun playPause() {
        if (audioService?.isPlaying!!.value) {
            audioService?.pause()
        } else {
            audioService?.play()
        }
    }

    fun stop() {
        audioService?.stop()
    }

    fun seekTo(positionMs: Int) {
        audioService?.seekTo(positionMs = positionMs)
    }

    fun setVolume(volume: Float) {
        audioService?.setVolume(volume = volume)
    }

    fun skipToNext(path: String) {
        audioService?.skipToNext(path = path)
    }

    fun skipToPrevious(path: String) {
        audioService?.skipToPrevious(path = path)
    }

    fun setDataSource(path: String) {
        audioService?.setDataSource(path = path)
    }
}
