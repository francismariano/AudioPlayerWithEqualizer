package me.francis.audioplayerwithequalizer;

// OBS: Talvez deva ser implementada em kotlin, testar
public class AudioEqualizer {
    static {
        System.loadLibrary("equalizer");
    }

    public native int applyEqualization(short[] audioData, int[] gains);
}
