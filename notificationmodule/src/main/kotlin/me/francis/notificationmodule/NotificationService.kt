package me.francis.notificationmodule


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.core.app.ServiceCompat
import androidx.media3.common.Player

import android.net.Uri

@UnstableApi
class NotificationService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private var currentMediaItem: MediaItem? = null

    override fun onCreate() {
        super.onCreate()

        println("onCreate NotificationService")

        // Inicializa o player
        player = ExoPlayer.Builder(this).build()

        // Inicializa a MediaSession
        mediaSession = MediaSession.Builder(this, player)
            .build()

        // Iniciar o player com uma musica mp3 do assets
        val assetFileName = "sample2.mp3" // Nome do arquivo na pasta assets
        val assetUri = getAssetUri(assetFileName) // Criar a Uri do assets
        val mediaItem = MediaItem.fromUri(assetUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        player.addListener(playerListener)

        createNotificationChannel()
    }

    // listener do player
    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            currentMediaItem = mediaItem
            updateNotification(this@NotificationService, mediaSession)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateNotification(this@NotificationService, mediaSession)
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            updateNotification(this@NotificationService, mediaSession)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    // metodo que atualiza a notification
    @OptIn(UnstableApi::class)
    private fun updateNotification(context: Context) {
        val currentMediaItem = currentMediaItem
        val title = currentMediaItem?.mediaMetadata?.title ?: "Nome da Música"
        val artist = currentMediaItem?.mediaMetadata?.artist ?: "Descrição da Música"

        val notification = NotificationCompat.Builder(context, "AudioService")
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(androidx.media3.ui.R.drawable.exo_styled_controls_speed)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()

        val notificationManager = getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(1, notification)

        // verifica se o player esta tocando ou esta em buffering
        if (player.isPlaying || player.playbackState == Player.STATE_BUFFERING) {
            // Inicia o serviço em primeiro plano se nao tiver sido inicializado
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceCompat.startForeground(this, 1, notification, 0)
            } else {
                startForeground(1, notification)
            }
        } else {
            stopForeground(STOP_FOREGROUND_DETACH)
        }
    }

    // criando o canal de notificação
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "AudioService",
                "Playback",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    //metodo que cria a uri para o assets
    private fun getAssetUri(assetFileName: String): Uri {
        return Uri.parse("file:///android_asset/$assetFileName")
    }
}
