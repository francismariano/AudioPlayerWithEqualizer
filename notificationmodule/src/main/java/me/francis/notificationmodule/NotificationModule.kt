package me.francis.notificationmodule

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class NotificationModule(
    private val context: Context,
    private var mediaSession: MediaSessionCompat?,
    private val notificationTargetProvider: NotificationTargetProvider,
) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var currentNotification: Notification? = null


    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Player de Música",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificações do player de música"
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun buildNotification(
        currentTrack: Uri?,
        isPlaying: Boolean,
    ): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Player de Música")
            .setContentText(getFileNameFromUri(currentTrack))
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(getContentIntent())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_previous,
                    "Anterior",
                    getPendingIntent(ACTION_SKIP_PREVIOUS)
                )
            )
            .addAction(
                NotificationCompat.Action(
                    if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                    if (isPlaying) "Pausar" else "Tocar",
                    getPendingIntent(if (isPlaying) ACTION_PAUSE else ACTION_PLAY)
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

    fun updateNotification(notification: Notification) {
        currentNotification = notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getFileNameFromUri(uri: Uri?): String {
        return uri?.lastPathSegment?.substringAfterLast('/') ?: "Arquivo desconhecido"
    }

    private fun getContentIntent(): PendingIntent {
        val intent = Intent(context, notificationTargetProvider.getMainActivityClass()).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, notificationTargetProvider.getPlayerServiceClass()).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val CHANNEL_ID = "music_player_channel"
        const val NOTIFICATION_ID = 101

        // Ações
        const val ACTION_PLAY = "com.example.mediaplayer.PLAY"
        const val ACTION_PAUSE = "com.example.mediaplayer.PAUSE"
        const val ACTION_SKIP_NEXT = "com.example.mediaplayer.SKIP_NEXT"
        const val ACTION_SKIP_PREVIOUS = "com.example.mediaplayer.SKIP_PREVIOUS"
    }
}