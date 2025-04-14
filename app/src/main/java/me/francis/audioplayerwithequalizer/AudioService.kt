package me.francis.audioplayerwithequalizer

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.francis.playbackmodule.PlaybackListener
import me.francis.playbackmodule.PlaybackModule

data class AudioState(
    val isPlaying: Boolean = false,
    val position: Int = 0,
    val duration: Int = 0,
)

class AudioService : Service() {


    private var binder = AudioBinder()
    private lateinit var playbackModule: PlaybackModule
    //    private lateinit var notificationModule: NotificationModule
//    private lateinit var equalizationModule: EqualizationModule

    private val _audioState = MutableStateFlow(AudioState())
    val audioState = _audioState.asStateFlow()

    inner class AudioBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("AudioService", "onBind")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AudioService", "onCreate")

        playbackModule = PlaybackModule(this).apply {
            initialize()
            addListener(object : PlaybackListener {
//                override fun onPlaybackStateChanged(isPlaying: Boolean) {
//                    notificationModule.updateNotification(isPlaying)
//                }

                override fun onPlaybackStateChanged(state: Boolean) {
                    Log.d("AudioService", "onPlaybackStateChanged")
                    _audioState.value = audioState.value.copy(isPlaying = state)
                }

                override fun onPlaybackStopped() {
                    Log.d("AudioService", "onPlaybackStopped")
                    _audioState.value = AudioState()
                }

                override fun onPositionChanged(position: Int, duration: Int) {
                    Log.d("AudioService", "onPositionChanged")
                    _audioState.value = audioState.value.copy(position = position, duration = duration)
                }
            })
        }

//        equalizationModule = EqualizationModule(playbackModule.getAudioSessionId())
//        notificationModule = NotificationModule(this, this)

//        startForeground(
//            notificationModule.createNotification("Ready", false)
//        )
    }

    // Métodos expostos
    fun prepare(path: String) = playbackModule.prepare(path)
    fun playPause() = playbackModule.playPause()//.also { notifyStateChanged() }
    fun stop() = playbackModule.stop()//.also { notifyStateChanged() }
    fun seekTo(position: Int) = playbackModule.seekTo(position)//.also { notifyStateChanged() }
    fun isPlaying(): Boolean = playbackModule.isPlaying()

    override fun onDestroy() {
        Log.d("AudioService", "onDestroy")
        playbackModule.release()
//        equalizationModule.release()
//        notificationModule.release()
        super.onDestroy()
    }
}