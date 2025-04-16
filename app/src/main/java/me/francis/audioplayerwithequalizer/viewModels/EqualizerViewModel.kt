package me.francis.audioplayerwithequalizer.viewModels

import androidx.lifecycle.ViewModel

class EqualizerViewModel(
) : ViewModel() {

    fun updateEqualizer(
        column: Int,
        frequency: Float
    ) {
        println("*** update Equalizer")
    }
}
