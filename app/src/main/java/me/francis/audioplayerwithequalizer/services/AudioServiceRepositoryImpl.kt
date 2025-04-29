package me.francis.audioplayerwithequalizer.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.francis.audioplayerwithequalizer.AudioEqualizer
import me.francis.playbackmodule.PlaybackEvent
import me.francis.playbackmodule.PlaybackModule
import me.francis.playbackmodule.PlaybackModuleImpl

class AudioServiceRepositoryImpl : Service(), PlaybackModule {

    private var playbackModule: PlaybackModule = PlaybackModuleImpl()
    private val audioEqualizer = AudioEqualizer()

    // ciclo de vida //
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {

            println("intent = $intent")

            when (intent.action) {
                "ACTION_PLAY" -> play()
                "ACTION_PAUSE" -> pause()
                "ACTION_STOP" -> stopSelf()
                "ACTION_SEEK_TO" -> {
                    val position = intent.getIntExtra("positionMs", -1)
                    if (position != -1) {
                        seekTo(position)
                    }
                }

                "ACTION_SET_VOLUME" -> {
                    val volume = intent.getFloatExtra("volume", -1f)
                    if (volume != -1f) {
                        setVolume(volume)
                    }
                }

                "ACTION_SKIP_TO_NEXT" -> {
                    val path = intent.getStringExtra("path")
                    path?.let { skipToNext(it) }
                }

                "ACTION_SKIP_TO_PREVIOUS" -> {
                    val path = intent.getStringExtra("path")
                    path?.let { skipToPrevious(it) }
                }

                "ACTION_EQUALIZE" -> {
                    val audioData = intent.getShortArrayExtra("audioData")
                    val gains = intent.getIntArrayExtra("gains")
                    if (audioData != null && gains != null) {
                        equalize(audioData, gains)
                    }
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    // playback module //
    override val currentPosition: StateFlow<Int> get() = playbackModule.currentPosition
    override val duration: StateFlow<Int> get() = playbackModule.duration
    override val isPlaying: StateFlow<Boolean> get() = playbackModule.isPlaying
    override val currentTrack: StateFlow<String?> get() = playbackModule.currentTrack
    override val playbackEvents: Flow<PlaybackEvent> get() = playbackModule.playbackEvents

    override fun play() = try {
        playbackModule.play()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun pause() = try {
        playbackModule.pause()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun stop() = try {
        playbackModule.stop()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun seekTo(positionMs: Int) = try {
        playbackModule.seekTo(positionMs)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun setVolume(volume: Float) = try {
        playbackModule.setVolume(volume)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun skipToNext(path: String) = try {
        playbackModule.skipToNext(path)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun skipToPrevious(path: String) = try {
        playbackModule.skipToPrevious(path)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun setDataSource(path: String) = try {
        playbackModule.setDataSource(path)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun release() = try {
        playbackModule.release()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // equalizer module //
    private fun equalize(
        audioData: ShortArray,
        gains: IntArray,
    ) = audioEqualizer.applyEqualization(audioData, gains)
}
