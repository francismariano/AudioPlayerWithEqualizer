package me.francis.notificationmodule

interface NotificationTargetProvider {
    fun getMainActivityClass(): Class<*>
    fun getPlayerServiceClass(): Class<*>
}