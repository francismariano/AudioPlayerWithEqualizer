package me.francis.equalizermodule

import kotlinx.coroutines.flow.StateFlow

interface EqualizerModule {

    val equalizerState: StateFlow<EqualizerState>

    fun setGlobalGain(gain: Float)
    fun setBandGain(band: Int, gain: Float)
    fun toggleEqualizer()

    fun resetEqualizer()
}