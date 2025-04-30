package me.francis.audioplayerwithequalizer.services

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.net.toUri

class AudioFileManager {
    companion object {
        var defaultMusicUri: Uri = "content://com.android.externalstorage.documents/tree/primary%3AMusic".toUri()
        var musicList = mutableListOf<Music>()
    }

    fun processAudioFiles(uri: Uri, contentResolver: ContentResolver) {
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
                val displayName = it.getString(1)
                val mimeType = it.getString(2)

                if (mimeType?.startsWith("audio/") == true) {
                    val fileUri = DocumentsContract.buildDocumentUriUsingTree(uri, documentId)
                    val path = fileUri.toString().removePrefix("content://...")
                    musicList.add(Music(index++, displayName ?: "Sem nome", path))
                }
            }
        }
    }

    data class Music(
        val index: Int,
        val nome: String,
        val path: String
    )
}
