package me.francis.playbackmodule

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class PlaybackModuleImpl(private val context: Context) : PlaybackModule {
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    val scope = CoroutineScope(Dispatchers.Default)

    init {
        mediaPlayer.setOnPreparedListener {
            _playbackState.value = _playbackState.value.copy(
                duration = it.duration,
                isReady = true
            )
            it.start()
            updateProgress()
        }

        mediaPlayer.setOnCompletionListener {
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                currentPosition = 0
            )
        }
    }

    private fun updateProgress() {
        scope.launch {
            while (mediaPlayer.isPlaying) {
                _playbackState.value = _playbackState.value.copy(
                    currentPosition = mediaPlayer.currentPosition
                )
                delay(500)
            }
        }
    }

    override fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
            updateProgress()
        }
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _playbackState.value = _playbackState.value.copy(isPlaying = false)
        }
    }

    override fun stop() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        _playbackState.value = PlaybackState()
    }

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
        _playbackState.value = _playbackState.value.copy(currentPosition = position)
    }

    override fun skipToNext() {
        // Implementação depende da lista de músicas
    }

    override fun skipToPrevious() {
        // Implementação depende da lista de músicas
    }

    override fun setDataSource(uri: Uri) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.prepareAsync()
            _playbackState.value = _playbackState.value.copy(
                isReady = false,
                currentPosition = 0
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun release() {
        mediaPlayer.release()
        scope.cancel()
    }
}