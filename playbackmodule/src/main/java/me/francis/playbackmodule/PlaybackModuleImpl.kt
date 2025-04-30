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
    private var currentPlaylist: List<Uri> = emptyList()
    private var currentTrackIndex: Int = -1

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

    fun setPlaylist(playlist: List<Uri>) {
        currentPlaylist = playlist
        currentTrackIndex = if (playlist.isNotEmpty()) 0 else -1
        if (currentTrackIndex != -1) {
            setDataSource(playlist[currentTrackIndex])
        }
    }

    override fun skipToNext() {
        if (currentPlaylist.isEmpty()) return

        currentTrackIndex = (currentTrackIndex + 1) % currentPlaylist.size
        setDataSource(currentPlaylist[currentTrackIndex])
        play()
    }

    override fun skipToPrevious() {
        if (currentPlaylist.isEmpty()) return

        currentTrackIndex = if (currentTrackIndex - 1 < 0) {
            currentPlaylist.size - 1
        } else {
            currentTrackIndex - 1
        }
        setDataSource(currentPlaylist[currentTrackIndex])
        play()
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

    fun getCurrentTrack(): Uri? {
        return if (currentTrackIndex in currentPlaylist.indices) {
            currentPlaylist[currentTrackIndex]
        } else {
            null
        }
    }

    fun release() {
        mediaPlayer.release()
        scope.cancel()
    }
}