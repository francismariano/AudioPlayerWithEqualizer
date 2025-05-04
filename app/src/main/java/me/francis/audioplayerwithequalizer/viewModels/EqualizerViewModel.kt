package me.francis.audioplayerwithequalizer.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class EqualizerViewModel(
    application: Application,
    private val playerController: MusicPlayerController
) : AndroidViewModel(application) {

    val equalizerState = playerController.equalizerState

    init {
        Log.d("EqualizerViewModel", "init: $playerController")
        playerController.connect()
    }

    fun setGlobalGain(gain: Float) = playerController.setGlobalGain(gain)
    fun setBandGain(band: Int, gain: Float) = playerController.setBandGain(band, gain)
    fun toggleEqualizer() = playerController.toggleEqualizer()
    fun resetEqualizer() = playerController.resetEqualizer()

    override fun onCleared() {
        super.onCleared()
    }
}