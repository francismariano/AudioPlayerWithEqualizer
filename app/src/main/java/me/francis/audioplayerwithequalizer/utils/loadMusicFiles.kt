package me.francis.audioplayerwithequalizer.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import me.francis.audioplayerwithequalizer.models.Music
import java.io.File

fun loadMusicFiles(context: Context): List<Music> {
    val musicDir = File(context.getExternalFilesDir(null), "Musics")
    if (!musicDir.exists()) return emptyList()

    return musicDir.listFiles()?.filter { file ->
        file.extension.lowercase() in listOf("mp3", "wav", "ogg", "m4a")
    }?.map { file ->
        Music(file.name, file.absolutePath)
    } ?: emptyList()
}