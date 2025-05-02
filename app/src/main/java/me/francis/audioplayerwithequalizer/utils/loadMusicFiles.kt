package me.francis.audioplayerwithequalizer.utils

import android.os.Environment
import me.francis.audioplayerwithequalizer.models.Music
import java.io.File

fun loadMusicFiles(): List<Music> {
    val musicDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    if (!musicDir.exists()) return emptyList()

    return musicDir.listFiles()?.filter { file ->
        file.extension.lowercase() in listOf("mp3", "wav", "ogg", "m4a")
    }?.map { file ->
        Music(file.name, file.absolutePath)
    } ?: emptyList()
}
