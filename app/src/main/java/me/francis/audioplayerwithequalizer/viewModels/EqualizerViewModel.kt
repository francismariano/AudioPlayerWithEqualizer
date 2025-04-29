package me.francis.audioplayerwithequalizer.viewModels

import androidx.lifecycle.ViewModel
import me.francis.audioplayerwithequalizer.models.Equalizer
import me.francis.audioplayerwithequalizer.services.AudioServiceRepository

class EqualizerViewModel(private val audioServiceRepository: AudioServiceRepository) : ViewModel() {

    fun equalize(equalizer: Equalizer) = audioServiceRepository.equalize(equalizer)

    // Todo: implementar UI
    fun setVolume(volume: Float) = audioServiceRepository.setVolume(volume)
}
