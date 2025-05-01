package me.francis.audioplayerwithequalizer.viewModels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

class MusicPlayerViewModel(
    application: Application,
    private val playerController: MusicPlayerController
) : AndroidViewModel(application) {

    val playbackState = playerController.playbackState

    init {
        playerController.connect()
    }

    fun play() = playerController.play()
    fun pause() = playerController.pause()
    fun skipNext() = playerController.skipNext()
    fun skipTo(position: Int) = playerController.skipTo(position)
    fun skipPrevious() = playerController.skipPrevious()
    fun seekTo(position: Int) = playerController.seekTo(position)
    fun setPlaylist(uris: List<Uri>) = playerController.setPlaylist(uris)

    override fun onCleared() {
        super.onCleared()
        playerController.disconnect()
    }
}