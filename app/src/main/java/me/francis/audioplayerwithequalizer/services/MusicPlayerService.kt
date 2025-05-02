package me.francis.audioplayerwithequalizer.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.francis.audioplayerwithequalizer.utils.AppNotificationTargetProvider
import me.francis.notificationmodule.NotificationModule
import me.francis.playbackmodule.PlaybackModuleImpl

class MusicPlayerService : Service() {
    private val binder = LocalBinder()
    val playbackModule = PlaybackModuleImpl(this)
    private var isServiceStarted = false
    private var mediaSession: MediaSessionCompat? = null
    private lateinit var notificationModule: NotificationModule

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private fun initializeMediaSession() {
        mediaSession = MediaSessionCompat(this, "MusicPlayerService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//            setCallback(MediaSessionCallback())
            setActive(true)
        }
    }

    private fun initializeNotificationModule() {
        notificationModule = NotificationModule(this, mediaSession, AppNotificationTargetProvider())
    }

    // Binder para comunicação com a Activity
    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        initializeMediaSession()
        initializeNotificationModule()

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
        }
    }

    private fun getFileNameFromUri(uri: Uri?): String {
        return uri?.lastPathSegment?.substringAfterLast('/') ?: "Arquivo desconhecido"
    }

    override fun onDestroy() {
        playbackModule.release()
        mediaSession?.release()
        coroutineScope.cancel()
        super.onDestroy()
    }

//    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
//        override fun onPlay() {
//            Log.d("MusicPlayerService", "onPlay() called")
//            // Implementar ação de play
//        }
//
//        override fun onPause() {
//            Log.d("MusicPlayerService", "onPause() called")
//            // Implementar ação de pause
//        }
//    }

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

        // Extras
        const val EXTRA_POSITION = "extra_position"
        const val EXTRA_INDEX = "extra_index"
        const val EXTRA_PLAYLIST = "extra_playlist"

        fun startService(context: Context, action: String, extras: Bundle? = null) {
            val intent = Intent(context, MusicPlayerService::class.java).apply {
                this.action = action
                extras?.let { putExtras(it) }
            }
            context.startService(intent)
        }
    }
}