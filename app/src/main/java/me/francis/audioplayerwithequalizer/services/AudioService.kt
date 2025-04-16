package me.francis.audioplayerwithequalizer.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import me.francis.playbackmodule.PlaybackEvent
import me.francis.playbackmodule.PlaybackModule
import me.francis.playbackmodule.PlaybackModuleImpl

class AudioService(context: Context) : Service(), PlaybackModule {
    private val playbackModule = PlaybackModuleImpl(context = context)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun play() = playbackModule.play()

    override fun pause() = playbackModule.pause()

    override fun stop() = playbackModule.stop()

    override fun seekTo(positionMs: Int) = playbackModule.seekTo(positionMs)

    override fun setVolume(volume: Float) = playbackModule.setVolume(volume)

    override fun skipToNext() = playbackModule.skipToNext()

    override fun skipToPrevious() = playbackModule.skipToPrevious()

    override val currentPosition: StateFlow<Int>
        get() = playbackModule.currentPosition

    override val duration: StateFlow<Int>
        get() = playbackModule.duration

    override val isPlaying: StateFlow<Boolean>
        get() = playbackModule.isPlaying

    override val currentTrack: StateFlow<String?>
        get() = playbackModule.currentTrack

    override val playbackEvents: Flow<PlaybackEvent>
        get() = playbackModule.playbackEvents

    override fun setDataSource(path: String) = playbackModule.setDataSource(path)
}
