package me.francis.audioplayerwithequalizer.services

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import me.francis.audioplayerwithequalizer.services.AudioFileManager.Music

private var directoryPickerLauncher: ActivityResultLauncher<Uri?>? = null
var default_path = "content://com.android.externalstorage.documents/tree/primary%3AMusic"
val musicList = mutableListOf<Music>()

class AudioFileManager() {
    fun setComponet(directory: ActivityResultLauncher<Uri?>) {
        directoryPickerLauncher = directory
    }

    fun getDirectory() {
        directoryPickerLauncher?.launch(default_path.toUri())
    }

    fun processAudioFiles(uri: Uri, contentResolver: ContentResolver): List<Music> {
        var index = 0

        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri,
            DocumentsContract.getTreeDocumentId(uri)
        )

        val cursor = contentResolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
            ),
            null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val documentId = it.getString(0)
                val nome = it.getString(1)
                val mimeType = it.getString(2)

                if (mimeType.startsWith("audio/")) {
                    val fileUri = DocumentsContract.buildDocumentUriUsingTree(uri, documentId)
                    val path = fileUri.toString()
                    musicList.add(Music(index++, nome, path))
                }
            }
        }

        if (musicList.isNotEmpty()) {
            musicList.forEach {
                println("*** Music = ${Music(it.index, it.nome, it.path)} ***")
            }
        }

        return musicList
    }

    data class Music(
        val index: Int,
        val nome: String,
        val path: String
    )
}
