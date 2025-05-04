package me.francis.audioplayerwithequalizer.viewModels

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.francis.audioplayerwithequalizer.services.MusicPlayerService
import me.francis.equalizermodule.EqualizerModule
import me.francis.equalizermodule.EqualizerState
import me.francis.playbackmodule.PlaybackState

class MusicPlayerController(
    private val context: Context
) : EqualizerModule {
    private var serviceConnection: ServiceConnection? = null
    private var boundService: MusicPlayerService? = null
    private var isBound = false

    // TODO: verificar a possibilidade de passar para o viewmodel
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    // TODO: verificar a possibilidade de passar para o viewmodel
    private val _equalizerState = MutableStateFlow(EqualizerState())
    override val equalizerState: StateFlow<EqualizerState> = _equalizerState

    fun connect() {
        if (isBound) return

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicPlayerService.LocalBinder
                boundService = binder.getService()
                isBound = true
                startObservingState()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
                boundService = null
            }
        }

        Intent(context, MusicPlayerService::class.java).also { intent ->
            context.startService(intent)
            context.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
        }
    }

    private fun startObservingState() {
        boundService?.playbackModule?.playbackState?.let { flow ->
            coroutineScope.launch {
                flow.collect { state ->
                    _playbackState.value = state
                }
            }
        }

        boundService?.equalizerModule?.equalizerState?.let { flow ->
            coroutineScope.launch {
                flow.collect { state ->
                    _equalizerState.value = state
                }
            }
        }
    }

    fun disconnect() {
        coroutineScope.cancel()
        serviceConnection?.let { connection ->
            if (isBound) {
                context.unbindService(connection)
                isBound = false
            }
        }
        boundService = null
    }

    // Métodos de controle do player
    fun play() {
        Log.d("MusicPlayerController", "play called")
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_PLAY)
    }

    fun pause() {
        Log.d("MusicPlayerController", "pause called")
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_PAUSE)
    }

    fun skipNext() {
        Log.d("MusicPlayerController", "skipNext called")
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_SKIP_NEXT)
    }

    fun skipTo(index: Int) {
        Log.d("MusicPlayerController", "skipTo called with index: $index")
        MusicPlayerService.startService(
            context, MusicPlayerService.ACTION_SKIP_TO,
            Bundle().apply {
                putInt(MusicPlayerService.EXTRA_INDEX, index)
            }
        )
    }

    fun skipPrevious() {
        Log.d("MusicPlayerController", "skipPrevious called")
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_SKIP_PREV)
    }

    fun seekTo(position: Int) {
        Log.d("MusicPlayerController", "seekTo called with position: $position")
        MusicPlayerService.startService(
            context, MusicPlayerService.ACTION_SEEK_TO,
            Bundle().apply {
                putInt(MusicPlayerService.EXTRA_POSITION, position)
            }
        )
    }

    fun setPlaylist(uris: List<Uri>) {
        Log.d("MusicPlayerController", "setPlaylist called with uris: $uris")
        MusicPlayerService.startService(
            context, MusicPlayerService.ACTION_SET_PLAYLIST,
            Bundle().apply {
                putParcelableArrayList(MusicPlayerService.EXTRA_PLAYLIST, ArrayList(uris))
            }
        )
    }

    // Métodos do Equalizer
    override fun setGlobalGain(gain: Float) {
        Log.d("MusicPlayerController", "setGlobalGain called with gain: $gain")
        MusicPlayerService.startService(
            context, MusicPlayerService.ACTION_SET_GLOBAL_GAIN,
            Bundle().apply {
                putFloat(MusicPlayerService.EXTRA_GAIN, gain)
            }
        )
    }

    override fun setBandGain(band: Int, gain: Float) {
        Log.d("MusicPlayerController", "setBandGain called with band: $band, gain: $gain")
        MusicPlayerService.startService(
            context, MusicPlayerService.ACTION_SET_BAND_GAIN,
            Bundle().apply {
                putInt(MusicPlayerService.EXTRA_BAND, band)
                putFloat(MusicPlayerService.EXTRA_GAIN, gain)
            }
        )
    }

    override fun toggleEqualizer() {
        Log.d("MusicPlayerController", "toggleEqualizer called")
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_TOGGLE_EQUALIZER)
    }

    override fun resetEqualizer() {
        Log.d("MusicPlayerController", "resetEqualizer called")
        MusicPlayerService.startService(context, MusicPlayerService.ACTION_RESET_EQUALIZER)
    }

}