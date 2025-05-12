package me.francis.playbackmodule

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

open class PlaybackModuleImpl(
    private val context: Context,
    open var mediaPlayer: MediaPlayer = MediaPlayer() // permite mock nos testes
) : PlaybackModule {
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState
    private var currentPlaylist: List<Uri> = emptyList()
    private var currentTrackIndex: Int = -1

    val scope = CoroutineScope(Dispatchers.Default)

    init {
        mediaPlayer.setOnPreparedListener {
            Log.d("MediaPlayer*", "preparedListener")
            _playbackState.value = _playbackState.value.copy(
                duration = it.duration,
                isReady = true,
                isPlaying = true
            )
            it.start()
            updateProgress()
        }

        mediaPlayer.setOnCompletionListener {
            skipToNext()
        }

    }

    private fun updateProgress() {
        Log.d("MediaPlayer*", "Updating progress")
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
        Log.d("MediaPlayer*", "play() called")
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
            updateProgress()
        }
    }

    override fun pause() {
        Log.d("MediaPlayer*", "pause() called")
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _playbackState.value = _playbackState.value.copy(isPlaying = false)
        }
    }

    override fun stop() {
        Log.d("MediaPlayer*", "stop() called")
        mediaPlayer.stop()
        mediaPlayer.reset()
        _playbackState.value = PlaybackState()
    }

    override fun seekTo(position: Int) {
        Log.d("MediaPlayer*", "seekTo() called with position: $position")
        mediaPlayer.seekTo(position)
        _playbackState.value = _playbackState.value.copy(currentPosition = position)
    }

    fun setPlaylist(playlist: List<Uri>) {
        Log.d("MediaPlayer*", "setPlaylist() called with playlist: $playlist")
        currentPlaylist = playlist
        currentTrackIndex = if (playlist.isNotEmpty()) 0 else -1
        if (currentTrackIndex != -1) {
            setDataSource(playlist[currentTrackIndex])
        }
    }

    override fun skipTo(index: Int) {
        Log.d("MediaPlayer*", "skipTo() called with index: $index")
        if (index in currentPlaylist.indices) {
            currentTrackIndex = index
            setDataSource(currentPlaylist[currentTrackIndex])
//            play()
        }
    }

    override fun skipToNext() {
        Log.d("MediaPlayer*", "skipToNext() called")
        if (currentPlaylist.isEmpty()) return

        currentTrackIndex = (currentTrackIndex + 1) % currentPlaylist.size
        setDataSource(currentPlaylist[currentTrackIndex])
//        play()
    }

    override fun skipToPrevious() {
        Log.d("MediaPlayer*", "skipToPrevious() called")
        if (currentPlaylist.isEmpty()) return

        currentTrackIndex = if (currentTrackIndex - 1 < 0) {
            currentPlaylist.size - 1
        } else {
            currentTrackIndex - 1
        }
        setDataSource(currentPlaylist[currentTrackIndex])
//        play()
    }

    override fun setDataSource(uri: Uri) {
        Log.d("MediaPlayer*", "setDataSource() called with uri: $uri")
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.prepareAsync()
            _playbackState.value = _playbackState.value.copy(
                isReady = false,
                currentPosition = 0,
                playlistSize = currentPlaylist.size,
                currentTrack = uri
            )
        } catch (e: IOException) {
            Log.d("MediaPlayer*", "Error setting data source", e)
            e.printStackTrace()
        }
    }

    fun release() {
        Log.d("MediaPlayer*", "release() called")
        mediaPlayer.release()
        scope.cancel()
    }

    fun getCurrentTrackIndex(): Int {
        return currentTrackIndex
    }
}