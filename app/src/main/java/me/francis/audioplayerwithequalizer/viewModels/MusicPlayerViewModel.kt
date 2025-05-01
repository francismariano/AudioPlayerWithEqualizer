package me.francis.audioplayerwithequalizer.viewModels

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.francis.audioplayerwithequalizer.services.MusicPlayerService
import me.francis.playbackmodule.PlaybackState

class MusicPlayerViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private var serviceConnection: ServiceConnection? = null
    private var boundService: MusicPlayerService? = null
    private var isBound = false

    init {
        connectToService()
    }

    private fun connectToService() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicPlayerService.LocalBinder
                boundService = binder.getService()
                isBound = true
                startObservingServiceState()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
                boundService = null
            }
        }

        // Inicia e conecta ao serviço
        context.startService(Intent(context, MusicPlayerService::class.java))
        context.bindService(
            Intent(context, MusicPlayerService::class.java),
            serviceConnection!!,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun startObservingServiceState() {
        viewModelScope.launch {
            while (isBound) {
                boundService?.playbackModule?.let { module ->
                    _playbackState.value = module.getCurrentState()
                }
                delay(500) // Atualiza a cada 500ms
            }
        }
    }

    fun play() {
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_PLAY)
    }

    fun pause() {
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_PAUSE)
    }

    fun skipNext() {
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_SKIP_NEXT)
    }

    fun skipPrevious() {
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_SKIP_PREV)
    }

    fun seekTo(position: Int) {
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_SEEK_TO,
            Bundle().apply {
                putInt(MusicPlayerService.EXTRA_POSITION, position)
            }
        )
    }

    fun setPlaylist(uris: List<Uri>) {
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_SET_PLAYLIST,
            Bundle().apply {
                putParcelableArrayList(MusicPlayerService.EXTRA_PLAYLIST, ArrayList(uris))
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection?.let {
            if (isBound) {
                context.unbindService(it)
            }
        }
    }
}