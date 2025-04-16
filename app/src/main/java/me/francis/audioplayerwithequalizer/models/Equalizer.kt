package me.francis.audioplayerwithequalizer.models

data class Equalizer(
    val name: String = "Equalizer",
    val level: Int = 10,
    val frequencies: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
)
