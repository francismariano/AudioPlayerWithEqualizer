package me.francis.audioplayerwithequalizer

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import me.francis.playbackmodule.PlaybackModule
import androidx.core.net.toUri

@Composable
fun AudioPlayerScreen(
    audioService: StateFlow<AudioService?>
) {
    val _audioService = audioService.collectAsState()
    val state = _audioService.value?.audioState?.collectAsState()
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    var selectedFile = "android.resource://${LocalContext.current.packageName}/${R.raw.sample1}".toUri().toString()

    // Atualiza o estado quando o player muda
    LaunchedEffect(Unit) {
        _audioService.value?.prepare(selectedFile)

//        playbackModule.addListener(object : PlaybackListener {
//            override fun onPlaybackStarted() {
//                isPlaying = true
//            }
//            override fun onPlaybackPaused() {
//                isPlaying = false
//            }
//            override fun onPlaybackStopped() {
//                currentPosition = 0
//            }
//            override fun onPositionChanged(position: Int, totalDuration: Int) {
//                currentPosition = position
//                duration = totalDuration
//            }
//        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Barra de progresso
        Slider(
            value = state?.value?.position?.toFloat() ?: 222f,
            onValueChange = { newValue ->
                currentPosition = newValue.toInt()
            },
            onValueChangeFinished = {
                _audioService.value?.seekTo(currentPosition)
            },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        // Tempo decorrido/total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = currentPosition.toTimeFormat())
            Text(text = duration.toTimeFormat())
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Controles
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botão play/pause
            Button(
                onClick = {
                    _audioService.value?.playPause()
                },
                modifier = Modifier.size(64.dp)
            ) {
                Text(
                    text = if (_audioService.value?.isPlaying() == true) "Pause" else "PLay",
                    modifier = Modifier.size(48.dp)
                )
            }

            // Botão stop
            IconButton(onClick = {
                Log.d("AudioPlayerScreen", "Parando reprodução")
                _audioService.value?.stop()
                selectedFile = ""
            }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Parar")
            }
        }
    }
}

// Extensão para formatar tempo (mm:ss)
fun Int.toTimeFormat(): String {
    val minutes = (this / 1000) / 60
    val seconds = (this / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}