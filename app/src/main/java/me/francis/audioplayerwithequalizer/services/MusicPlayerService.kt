package me.francis.audioplayerwithequalizer.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.francis.audioplayerwithequalizer.utils.AppNotificationTargetProvider
import me.francis.equalizermodule.EqualizerModule
import me.francis.equalizermodule.EqualizerModuleImpl
import me.francis.notificationmodule.NotificationModule
import me.francis.playbackmodule.PlaybackModuleImpl

class MusicPlayerService : Service() {
    private val binder = LocalBinder()
    private var isServiceStarted = false
    lateinit var playbackModule: PlaybackModuleImpl
    private lateinit var notificationModule: NotificationModule
    lateinit var equalizerModule: EqualizerModule
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private fun initializePlaybackModule() {
        playbackModule = PlaybackModuleImpl(this)
    }

    private fun initializeNotificationModule() {
        notificationModule = NotificationModule(this, AppNotificationTargetProvider())
    }

    private fun initializeEqualizerModule() {
        equalizerModule = EqualizerModuleImpl()
    }

    // Binder para comunicação com a Activity
    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        initializePlaybackModule()
        initializeNotificationModule()
        initializeEqualizerModule()

        coroutineScope.launch {
            playbackModule.playbackState.distinctUntilChanged { old, new ->
                old.currentTrack == new.currentTrack && old.isPlaying == new.isPlaying
            }.collect { state ->
                notificationModule.updateNotification(
                    notificationModule.buildNotification(
                        currentTrack = state.currentTrack,
                        isPlaying = state.isPlaying,
                    )
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceStarted) {

            val notification = notificationModule.buildNotification(
                currentTrack = playbackModule.playbackState.value.currentTrack,
                isPlaying = playbackModule.playbackState.value.isPlaying,
            )

            startForeground(NOTIFICATION_ID, notification)
            isServiceStarted = true
        }

        // Processar ações recebidas
        intent?.action?.let { handleAction(it, intent) }

        return START_STICKY
    }

    private fun handleAction(action: String, intent: Intent) {
        when (action) {
            ACTION_PLAY -> playbackModule.play()
            ACTION_PAUSE -> playbackModule.pause()
            ACTION_STOP -> {
                playbackModule.stop()
                stopSelf()
            }

            ACTION_SKIP_NEXT -> playbackModule.skipToNext()
            ACTION_SKIP_TO -> intent.getIntExtra(EXTRA_INDEX, 0).let {
                playbackModule.skipTo(it)
            }

            ACTION_SKIP_PREV -> playbackModule.skipToPrevious()
            ACTION_SEEK_TO -> intent.getIntExtra(EXTRA_POSITION, 0).let {
                playbackModule.seekTo(it)
            }

            ACTION_SET_PLAYLIST -> {
                val uris = intent.getParcelableArrayListExtra<Uri>(EXTRA_PLAYLIST)
                uris?.let { playbackModule.setPlaylist(it) }
            }

            ACTION_SET_GLOBAL_GAIN -> intent.getFloatExtra(EXTRA_GAIN, 0f).let {
                equalizerModule.setGlobalGain(it)
            }

            ACTION_SET_BAND_GAIN -> {
                val band = intent.getIntExtra(EXTRA_BAND, 0)
                val gain = intent.getFloatExtra(EXTRA_GAIN, 0f)
                equalizerModule.setBandGain(band, gain)
            }

            ACTION_TOGGLE_EQUALIZER -> equalizerModule.toggleEqualizer()

            ACTION_RESET_EQUALIZER -> equalizerModule.resetEqualizer()
        }
    }

    override fun onDestroy() {
        playbackModule.release()
        notificationModule.release()
        coroutineScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "music_player_channel"
        const val NOTIFICATION_ID = 101

        // Ações
        const val ACTION_PLAY = "com.example.mediaplayer.PLAY"
        const val ACTION_PAUSE = "com.example.mediaplayer.PAUSE"
        const val ACTION_STOP = "com.example.mediaplayer.STOP"
        const val ACTION_SKIP_NEXT = "com.example.mediaplayer.SKIP_NEXT"
        const val ACTION_SKIP_TO = "com.example.mediaplayer.SKIP_TO"
        const val ACTION_SKIP_PREV = "com.example.mediaplayer.SKIP_PREV"
        const val ACTION_SEEK_TO = "com.example.mediaplayer.SEEK_TO"
        const val ACTION_SET_PLAYLIST = "com.example.mediaplayer.SET_PLAYLIST"
        const val ACTION_SET_GLOBAL_GAIN = "com.example.mediaplayer.SET_GLOBAL_GAIN"
        const val ACTION_SET_BAND_GAIN = "com.example.mediaplayer.SET_BAND_GAIN"
        const val ACTION_TOGGLE_EQUALIZER = "com.example.mediaplayer.TOGGLE_EQUALIZER"
        const val ACTION_RESET_EQUALIZER = "com.example.mediaplayer.RESET_EQUALIZER"

        // Extras
        const val EXTRA_POSITION = "extra_position"
        const val EXTRA_INDEX = "extra_index"
        const val EXTRA_PLAYLIST = "extra_playlist"
        const val EXTRA_GAIN = "extra_gain"
        const val EXTRA_BAND = "extra_band"

        fun startService(context: Context, action: String, extras: Bundle? = null) {
            val intent = Intent(context, MusicPlayerService::class.java).apply {
                this.action = action
                extras?.let { putExtras(it) }
            }
            context.startService(intent)
        }
    }
}