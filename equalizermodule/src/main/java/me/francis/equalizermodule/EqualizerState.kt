package me.francis.equalizermodule

data class EqualizerState(
    val isEnabled: Boolean = false,
    val globalGain: Float = 0f,
    val bandGains: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val bandsCount: Int = 5,
    val minGain: Float = -1.0f,
    val maxGain: Float = 1.0f
)