package me.francis.audioplayerwithequalizer.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.francis.playbackmodule.PlaybackEvent
import me.francis.playbackmodule.PlaybackModule
import me.francis.playbackmodule.PlaybackModuleImpl

class AudioService : Service(), PlaybackModule {

    private val binder = AudioBinder()
    private var playbackModule: PlaybackModule? = null

    override fun onCreate() {
        super.onCreate()
        playbackModule = PlaybackModuleImpl() // fazer assim ou criar diretamente?
        // todo: criar notificação aqui
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            val action = intent.action

            if (action != null) {
                when (action) {
                    "ACTION_PLAY" -> play()
                    "ACTION_PAUSE" -> pause()
                    "ACTION_STOP" -> stop()
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
        playbackModule = null
        // todo: destruir notificação aqui
    }

    inner class AudioBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(p0: Intent?): IBinder? = binder

    override fun play() = try {
        playbackModule!!.play()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun pause() = try {
        playbackModule!!.pause()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun stop() = try {
        playbackModule!!.stop()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun seekTo(positionMs: Int) = try {
        playbackModule!!.seekTo(positionMs)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun setVolume(volume: Float) = try {
        playbackModule!!.setVolume(volume)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun skipToNext(path: String) = try {
        playbackModule!!.skipToNext(path)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun skipToPrevious(path: String) = try {
        playbackModule!!.skipToPrevious(path)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun setDataSource(path: String) = try {
        playbackModule!!.setDataSource(path)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun release() = try {
        playbackModule!!.release()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override val currentPosition: StateFlow<Int> get() = playbackModule!!.currentPosition
    override val duration: StateFlow<Int> get() = playbackModule!!.duration
    override val isPlaying: StateFlow<Boolean> get() = playbackModule!!.isPlaying
    override val currentTrack: StateFlow<String?> get() = playbackModule!!.currentTrack
    override val playbackEvents: Flow<PlaybackEvent> get() = playbackModule!!.playbackEvents
}
