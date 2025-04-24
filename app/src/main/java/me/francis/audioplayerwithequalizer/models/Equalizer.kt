package me.francis.audioplayerwithequalizer.models

data class Equalizer(
    val level: Int = 10,
    val frequencies: ShortArray = shortArrayOf(0, 0, 0, 0, 0),
    val gains: IntArray = intArrayOf(0, 0, 0, 0, 0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Equalizer

        if (level != other.level) return false
        if (!frequencies.contentEquals(other.frequencies)) return false
        if (!gains.contentEquals(other.gains)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = level
        result = 31 * result + frequencies.contentHashCode()
        result = 31 * result + gains.contentHashCode()
        return result
    }
}
