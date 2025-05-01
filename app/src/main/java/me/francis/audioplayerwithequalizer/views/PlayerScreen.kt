package me.francis.audioplayerwithequalizer.views

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import me.francis.audioplayerwithequalizer.models.Music
import me.francis.audioplayerwithequalizer.utils.loadMusicFiles
import me.francis.audioplayerwithequalizer.viewModels.MusicPlayerViewModel

@Composable
fun PlayerScreen(
    viewModel: MusicPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val playbackState by viewModel.playbackState.collectAsState()
    val context = LocalContext.current
    var showPlaylistDialog by remember { mutableStateOf(false) }

    var musicList by remember { mutableStateOf<List<Music>>(emptyList()) }

    LaunchedEffect(Unit) {
        musicList = loadMusicFiles(context)
        viewModel.setPlaylist(musicList.map { it.path.toUri() })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // Área de informações da música
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = playbackState.currentTrack?.let { uri ->
                    getFileNameFromUri(context, uri)
                } ?: "Nenhuma música selecionada",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Barra de progresso
            Slider(
                value = playbackState.currentPosition.toFloat(),
                onValueChange = { viewModel.seekTo(it.toInt()) },
                valueRange = 0f..playbackState.duration.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )

            // Tempo decorrido/total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(playbackState.currentPosition))
                Text(formatTime(playbackState.duration))
            }
        }

        // Controles do player
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.skipPrevious() },
                enabled = playbackState.playlistSize > 0
            ) {
                Icon(Icons.Default.SkipPrevious, "Música anterior")
            }

            IconButton(
                onClick = {
                    if (playbackState.isPlaying) viewModel.pause() else viewModel.play()
                },
                enabled = playbackState.isReady
            ) {
                Icon(
                    if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    if (playbackState.isPlaying) "Pausar" else "Tocar"
                )
            }

            IconButton(
                onClick = { viewModel.skipNext() },
                enabled = playbackState.playlistSize > 0
            ) {
                Icon(Icons.Default.SkipNext, "Próxima música")
            }
        }

        // Botão para mostrar playlist
        Button(
            onClick = { showPlaylistDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Mostrar Playlist")
        }
    }

    // Diálogo da playlist
    if (showPlaylistDialog) {
        PlaylistDialog(
            onDismiss = { showPlaylistDialog = false },
            skipTo = viewModel::skipTo,
            musicList = musicList,
        )
    }
}

@Composable
private fun PlaylistDialog(
    onDismiss: () -> Unit,
    skipTo: (Int) -> Unit,
    musicList: List<Music> = emptyList(),
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Playlist") },
        text = {
            LazyColumn {
                items(musicList) { music ->
                    ListItem(
                        headlineContent = { Text(music.name) },
                        modifier = Modifier.clickable {
                            skipTo(musicList.indexOf(music))
                            onDismiss()
                        }
                    )
                    Divider()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

// Funções auxiliares
private fun formatTime(milliseconds: Int): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

private fun getFileNameFromUri(context: Context, uri: Uri): String {
    return uri.lastPathSegment?.substringAfterLast('/') ?: "Arquivo desconhecido"
}