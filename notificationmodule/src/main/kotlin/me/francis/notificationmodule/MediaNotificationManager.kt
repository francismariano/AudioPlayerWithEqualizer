package me.francis.notificationmodule

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaButtonReceiver
import androidx.media3.session.MediaSessionService

class MediaNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "media_playback_channel"
        const val NOTIFICATION_ID = 1
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    @UnstableApi
    fun buildNotification(mediaSession: MediaSessionService): Notification {
        // Example: Intent to open your main activity when the notification is tapped
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, NotificationService::class.java), // Replace MainActivity with your actual main activity
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Example: Previous action intent
        val previousIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, MediaButtonReceiver::class.java).setAction("ACTION_PREVIOUS"),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Example: Play/Pause action intent
        val playPauseIntent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, MediaButtonReceiver::class.java).setAction("ACTION_PLAY_PAUSE"),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Example: Next action intent
        val nextIntent = PendingIntent.getBroadcast(
            context,
            3,
            Intent(context, MediaButtonReceiver::class.java).setAction("ACTION_NEXT"),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_icon_next) // Replace with your actual icon
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, com.google.android.material.R.drawable.abc_ic_star_black_36dp)) // Replace with your music art
            .setContentTitle("Now Playing") // Replace with your actual content title
            .setContentText("Track Title") // Replace with your track title
            .setContentIntent(contentIntent)
//            .setStyle(
//                MediaStyle()
//                    .setMediaSession(mediaSession.sessionToken)
//                    .setShowActionsInCompactView(0, 1, 2) // Assuming you have 3 actions
//            )
//            .addAction(
//                NotificationCompat.Action.Builder(
//                    R.drawable.ic_previous, // Replace with your previous icon
//                    "Previous",
//                    previousIntent
//                ).build()
//            )
//            .addAction(
//                NotificationCompat.Action.Builder(
//                    R.drawable.ic_play_pause, // Replace with your play/pause icon
//                    "Play/Pause",
//                    playPauseIntent
//                ).build()
//            )
//            .addAction(
//                NotificationCompat.Action.Builder(
//                    R.drawable.ic_next, // Replace with your next icon
//                    "Next",
//                    nextIntent
//                ).build()
//            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Optional, set the notification's visibility on the lock screen

        return builder.build()
    }

    @OptIn(UnstableApi::class)
    fun showNotification(notification: Notification) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, NotificationService::class.java).setAction("START")
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}