package me.francis.audioplayerwithequalizer.utils

import me.francis.audioplayerwithequalizer.MainActivity
import me.francis.audioplayerwithequalizer.services.MusicPlayerService
import me.francis.notificationmodule.NotificationTargetProvider

class AppNotificationTargetProvider : NotificationTargetProvider {

    override fun getMainActivityClass(): Class<*> {
        return MainActivity::class.java
    }

    override fun getPlayerServiceClass(): Class<*> {
        return MusicPlayerService::class.java
    }

}