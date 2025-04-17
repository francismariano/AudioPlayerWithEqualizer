package me.francis.notificationmodule

import kotlinx.coroutines.flow.StateFlow

interface NotificationModule {
    // Controles básicos
    fun play()
    fun pause()
    fun stop()
    fun skipToNext()
    fun skipToPrevious()

    // Propriedades
    val musicName: StateFlow<String>
    val musicDescription: StateFlow<String>
}