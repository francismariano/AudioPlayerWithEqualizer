package me.francis.audioplayerwithequalizer

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@Composable
fun RawAudioPlayer() {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (isPlaying) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                isPlaying = false
            } else {
                val uri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample1}").toString()
//                val uri = Uri.parse("android.resource://me.francis.audioplayerwithequalizer/R.raw.sample1")
                val mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, uri.toUri())
                    prepare()
                    start()
                }
                isPlaying = true
            }
        }) {
            Text(if (isPlaying) "Parar música" else "Tocar música")
        }
    }
}