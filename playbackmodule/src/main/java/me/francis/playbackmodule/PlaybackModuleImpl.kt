package me.francis.playbackmodule

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class PlaybackModuleImpl(context: Context) : PlaybackModule {
    private val mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
    }

    private val scope = CoroutineScope(Dispatchers.Main)
    private var positionUpdateJob: Job? = null

    // Estados
    private val _currentPosition = MutableStateFlow(0)
    private val _duration = MutableStateFlow(0)
    private val _isPlaying = MutableStateFlow(false)
    private val _currentTrack = MutableStateFlow<String?>(null)
    private val _playbackEvents = MutableSharedFlow<PlaybackEvent>(extraBufferCapacity = 10)

    override val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()
    override val duration: StateFlow<Int> = _duration.asStateFlow()
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    override val currentTrack: StateFlow<String?> = _currentTrack.asStateFlow()
    override val playbackEvents: Flow<PlaybackEvent> = _playbackEvents.asSharedFlow()

    init {
        setupMediaPlayerListeners()
    }

    private fun setupMediaPlayerListeners() {
        mediaPlayer.setOnPreparedListener {
            _duration.value = mediaPlayer.duration
            startPositionUpdates()
            _playbackEvents.tryEmit(PlaybackEvent.PlaybackPrepared)
        }

        mediaPlayer.setOnCompletionListener {
            _isPlaying.value = false
            stopPositionUpdates()
            _playbackEvents.tryEmit(PlaybackEvent.PlaybackCompleted)
        }

        mediaPlayer.setOnErrorListener { _, what, extra ->
            val errorMsg = when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> "Erro desconhecido"
                MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Serviço de mídia indisponível"
                else -> "Erro code $what, extra $extra"
            }
            _playbackEvents.tryEmit(PlaybackEvent.Error(errorMsg))
            false
        }
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = scope.launch {
            while (isPlaying.value) {
                _currentPosition.value = mediaPlayer.currentPosition
                delay(1000) // Atualiza a cada segundo
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override fun play() {
        if (!_isPlaying.value) {
            mediaPlayer.start()
            _isPlaying.value = true
            startPositionUpdates()
            _playbackEvents.tryEmit(PlaybackEvent.PlaybackStarted)
        }
    }

    override fun pause() {
        if (_isPlaying.value) {
            mediaPlayer.pause()
            _isPlaying.value = false
            stopPositionUpdates()
            _playbackEvents.tryEmit(PlaybackEvent.PlaybackPaused)
        }
    }

    override fun stop() {
        mediaPlayer.stop()
        _isPlaying.value = false
        _currentPosition.value = 0
        stopPositionUpdates()
        _playbackEvents.tryEmit(PlaybackEvent.PlaybackStopped)
    }

    override fun seekTo(positionMs: Int) {
        if (positionMs in 0..duration.value) {
            mediaPlayer.seekTo(positionMs)
            _currentPosition.value = positionMs
        }
    }

    override fun skipToNext(path: String) {
        setDataSource(path)
        play()
    }

    override fun skipToPrevious(path: String) {
        setDataSource(path)
        play()
    }

    override fun setVolume(volume: Float) {
        mediaPlayer.setVolume(volume, volume)
    }

    override fun setDataSource(path: String) {
        try {
            _isPlaying.value = false
            mediaPlayer.reset()
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepareAsync() // Preparação assíncrona

            val trackName = path.substringAfterLast('/')
            _currentTrack.value = trackName
            _playbackEvents.tryEmit(PlaybackEvent.TrackChanged(trackName))
        } catch (e: IOException) {
            _playbackEvents.tryEmit(PlaybackEvent.Error("Falha ao carregar arquivo: ${e.message}"))
        } catch (e: IllegalStateException) {
            _playbackEvents.tryEmit(PlaybackEvent.Error("Player em estado inválido"))
        }
    }

    fun release() {
        stop()
        mediaPlayer.release()
        positionUpdateJob?.cancel()
    }
}