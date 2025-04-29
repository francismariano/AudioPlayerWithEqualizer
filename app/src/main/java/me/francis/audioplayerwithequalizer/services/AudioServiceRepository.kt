package me.francis.audioplayerwithequalizer.services

import android.app.Application
import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.francis.audioplayerwithequalizer.models.Equalizer
import me.francis.playbackmodule.PlaybackEvent
import me.francis.playbackmodule.PlaybackModule

class AudioServiceRepository(
    private val application: Application,
    private val audioServiceRepositoryImpl: AudioServiceRepositoryImpl,
) : PlaybackModule {

    override fun play() {
        val intent = Intent(application, AudioServiceRepositoryImpl::class.java).apply {
            action = "ACTION_PLAY"
        }
        application.startService(intent)
    }

    override fun pause() {
        val intent = Intent(application, AudioServiceRepositoryImpl::class.java).apply {
            action = "ACTION_PAUSE"
        }
        application.startService(intent)
    }

    override fun stop() {
        val intent = Intent(application, AudioServiceRepositoryImpl::class.java).apply {
            action = "ACTION_STOP"
        }
        application.startService(intent)
    }

    override fun seekTo(positionMs: Int) {
        val intent = Intent(application, AudioServiceRepositoryImpl::class.java).apply {
            action = "ACTION_SEEK_TO"
            putExtra("positionMs", positionMs)
        }
        application.startService(intent)
    }

    override fun setVolume(volume: Float) {
        val intent = Intent(application, AudioServiceRepositoryImpl::class.java).apply {
            action = "ACTION_SET_VOLUME"
            putExtra("volume", volume)
        }
        application.startService(intent)
    }

    override fun skipToNext(path: String) {
        val intent = Intent(application, AudioServiceRepositoryImpl::class.java).apply {
            action = "ACTION_SKIP_TO_NEXT"
            putExtra("path", path)
        }
        application.startService(intent)
    }

    override fun skipToPrevious(path: String) {
        val intent = Intent(application, AudioServiceRepositoryImpl::class.java).apply {
            action = "ACTION_SKIP_TO_PREVIOUS"
            putExtra("path", path)
        }
        application.startService(intent)
    }

    override fun release() = audioServiceRepositoryImpl.release()

    override val currentPosition: StateFlow<Int> = audioServiceRepositoryImpl.currentPosition
    override val duration: StateFlow<Int> = audioServiceRepositoryImpl.duration
    override val isPlaying: StateFlow<Boolean> = audioServiceRepositoryImpl.isPlaying
    override val currentTrack: StateFlow<String?> = audioServiceRepositoryImpl.currentTrack
    override val playbackEvents: Flow<PlaybackEvent> = audioServiceRepositoryImpl.playbackEvents

    override fun setDataSource(path: String) {
//        val intent = Intent(application, AudioService::class.java).apply {
//            action = "ACTION_SET_DATA_SOURCE"
//            putExtra("path", path)
//        }
//        application.startService(intent)
        TODO("Not yet implemented - esperando parte das musicas")
    }

    fun equalize(equalizer: Equalizer) {
        val intent = Intent(application, AudioServiceRepositoryImpl::class.java).apply {
            action = "ACTION_EQUALIZE"
            putExtra("audioData", equalizer.frequencies)
            putExtra("gains", equalizer.gains)
        }
        application.startService(intent)
    }
}
