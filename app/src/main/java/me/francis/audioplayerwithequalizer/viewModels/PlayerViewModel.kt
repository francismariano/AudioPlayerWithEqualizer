package me.francis.audioplayerwithequalizer.viewModels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import me.francis.audioplayerwithequalizer.services.AudioService

class PlayerViewModel(private val application: Application) : ViewModel() {

    fun play() {
        val intent = Intent(application, AudioService::class.java).apply {
            action = "ACTION_PLAY"
        }
        application.startService(intent)
    }

    fun pause() {
        val intent = Intent(application, AudioService::class.java).apply {
            action = "ACTION_PAUSE"
        }
        application.startService(intent)
    }

    fun seekTo(positionMs: Int) {
        val intent = Intent(application, AudioService::class.java).apply {
            action = "ACTION_SEEK_TO"
            putExtra("positionMs", positionMs)
        }
        application.startService(intent)
    }

    fun setVolume(volume: Float) {
        val intent = Intent(application, AudioService::class.java).apply {
            action = "ACTION_SET_VOLUME"
            putExtra("volume", volume)
        }
        application.startService(intent)
    }

    fun skipToNext(path: String) {
        val intent = Intent(application, AudioService::class.java).apply {
            action = "ACTION_SKIP_TO_NEXT"
            putExtra("path", path)
        }
        application.startService(intent)
    }

    fun skipToPrevious(path: String) {
        val intent = Intent(application, AudioService::class.java).apply {
            action = "ACTION_SKIP_TO_PREVIOUS"
            putExtra("path", path)
        }
        application.startService(intent)
    }
}
