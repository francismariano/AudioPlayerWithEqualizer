package me.francis.audioplayerwithequalizer

class AudioEqualizer {
    companion object {
        init {
            System.loadLibrary("equalizer")
        }
    }

    external fun applyEqualization(audioData: ShortArray, gains: IntArray): Int
}
