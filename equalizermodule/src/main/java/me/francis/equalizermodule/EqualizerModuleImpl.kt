package me.francis.equalizermodule

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EqualizerModuleImpl : EqualizerModule {

    private val _equalizerState = MutableStateFlow(EqualizerState())
    override val equalizerState: StateFlow<EqualizerState> = _equalizerState

    // Ponteiro para o objeto nativo
    private var nativePtr: Long = 0

    init {
        Log.d("EqualizerModule", "EqualizerModuleImpl initialized")
        System.loadLibrary("equalizer-jni")
        nativePtr = nativeInit()
    }

    override fun setGlobalGain(gain: Float) {
        Log.d("EqualizerModule", "setGlobalGain() called with gain: $gain")
        _equalizerState.value = _equalizerState.value.copy(globalGain = gain)
//        nativeSetGlobalGain(nativePtr, gain)
    }

    override fun setBandGain(band: Int, gain: Float) {
        Log.d("EqualizerModule", "setBandGain() called with band: $band, gain: $gain")
        _equalizerState.value = _equalizerState.value.copy(
            bandGains = _equalizerState.value.bandGains.toMutableList().apply {
                this[band] = gain
            })
//        nativeSetBandGain(nativePtr, band, gain)
    }

    override fun toggleEqualizer() {
        Log.d("EqualizerModule", "toggle() called")
        _equalizerState.value =
            _equalizerState.value.copy(isEnabled = !_equalizerState.value.isEnabled)
//        nativeSetEnabled(nativePtr, _equalizerState.value.isEnabled)
    }

    override fun resetEqualizer() {
        Log.d("EqualizerModule", "reset() called")
        _equalizerState.value = EqualizerState()
//        nativeReset(nativePtr)
    }

    // Funções nativas
    private external fun nativeInit(): Long
//    private external fun nativeSetGlobalGain(ptr: Long, gain: Float)
//    private external fun nativeSetBandGain(ptr: Long, band: Int, gain: Float)
//    private external fun nativeSetEnabled(ptr: Long, enabled: Boolean)
//    private external fun nativeReset(ptr: Long)

}