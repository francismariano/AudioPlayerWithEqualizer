package me.francis.audioplayerwithequalizer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.francis.audioplayerwithequalizer.services.AudioServiceRepository

class PlayerViewModelFactory(private val audioServiceRepository: AudioServiceRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(audioServiceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class EqualizerViewModelFactory(private val audioServiceRepository: AudioServiceRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EqualizerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EqualizerViewModel(audioServiceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
