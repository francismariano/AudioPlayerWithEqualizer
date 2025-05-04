package me.francis.equalizermodule

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EqualizerModuleImpl : EqualizerModule {

    private val _equalizerState = MutableStateFlow(EqualizerState())
    override val equalizerState: StateFlow<EqualizerState> = _equalizerState

    init {
        Log.d("EqualizerModule", "EqualizerModuleImpl initialized")
    }

    override fun setGlobalGain(gain: Float) {
        Log.d("EqualizerModule", "setGlobalGain() called with gain: $gain")
        _equalizerState.value = _equalizerState.value.copy(globalGain = gain)
    }

    override fun setBandGain(band: Int, gain: Float) {
        Log.d("EqualizerModule", "setBandGain() called with band: $band, gain: $gain")
        _equalizerState.value = _equalizerState.value.copy(
            bandGains = _equalizerState.value.bandGains.toMutableList().apply {
                this[band] = gain
            })
    }

    override fun toggleEqualizer() {
        Log.d("EqualizerModule", "toggle() called")
        _equalizerState.value =
            _equalizerState.value.copy(isEnabled = !_equalizerState.value.isEnabled)
    }

    override fun resetEqualizer() {
        Log.d("EqualizerModule", "reset() called")
        _equalizerState.value = EqualizerState()
    }
}