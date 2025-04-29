package me.francis.audioplayerwithequalizer.services

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import me.francis.audioplayerwithequalizer.services.AudioFileManager.Music

var default_path = "content://com.android.externalstorage.documents/tree/primary%3AMusic"
val musicList = mutableListOf<Music>()

class AudioFileManager(private val contentResolver: ContentResolver) {
    fun processAudioFiles(uri: Uri): List<Music> {

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
