package me.francis.audioplayerwithequalizer.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import me.francis.audioplayerwithequalizer.MainActivity
import me.francis.playbackmodule.PlaybackModuleImpl

class MusicPlayerService : Service() {
    private val binder = LocalBinder()
    val playbackModule = PlaybackModuleImpl(this)
    private var isServiceStarted = false

    private var mediaSession: MediaSessionCompat? = null

    private fun initializeMediaSession() {
        mediaSession = MediaSessionCompat(this, "MusicPlayerService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(MediaSessionCallback())
            setActive(true)
        }
    }

    // Criação do canal de notificação
    private fun createNotificationChannel() {
        Log.d("MusicPlayerService", "createNotificationChannel() called")
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Player de Música",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificação do player de música"
        }

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    // Binder para comunicação com a Activity
    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceStarted) {
            startForeground(NOTIFICATION_ID, buildNotification())
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

    private fun buildNotification(): Notification {
        Log.d("MusicPlayerService", "buildNotification() called")
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Player de Música")
            .setContentText("Tocando agora")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
//            .setLargeIcon(getAlbumArtBitmap()) // Bitmap da capa do álbum
            .setContentIntent(getContentIntent())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession?.sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_previous,
                    "Anterior",
                    getPendingIntent(ACTION_SKIP_PREV)
                )
            )
            .addAction(
                NotificationCompat.Action(
                    if (true) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                    if (true) "Pausar" else "Tocar",
                    getPendingIntent(if (true) ACTION_PAUSE else ACTION_PLAY)
                )
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_next,
                    "Próxima",
                    getPendingIntent(ACTION_SKIP_NEXT)
                )
            )
            .build()
    }

    private fun getContentIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        playbackModule.release()
        mediaSession?.release()
        super.onDestroy()
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            // Implementar ação de play
        }

        override fun onPause() {
            // Implementar ação de pause
        }
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