package me.francis.audioplayerwithequalizer.views

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import me.francis.audioplayerwithequalizer.R
import me.francis.audioplayerwithequalizer.models.Music
import me.francis.audioplayerwithequalizer.utils.loadMusicFiles
import me.francis.audioplayerwithequalizer.viewModels.MusicPlayerViewModel

@Composable
fun MusicPlayerView(
    navController: NavController,
    musicPlayerViewModel: MusicPlayerViewModel
) {
    val playbackState by musicPlayerViewModel.playbackState.collectAsState()
    var showPlaylistDialog by remember { mutableStateOf(false) }

    var musicList by remember { mutableStateOf<List<Music>>(emptyList()) }

    LaunchedEffect(Unit) {
        musicList = loadMusicFiles()
        musicPlayerViewModel.setPlaylist(musicList.map { it.path.toUri() })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff35345b))
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.height(20.dp)) {}

        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {  }, // nova pasta de musica
                enabled = true
            ) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = Color(0xffa8a8c7)
                )
            }

            IconButton(
                onClick = { navController.navigate("equalizer") },
                enabled = true
            ) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    painter = painterResource(R.drawable.equalizer),
                    contentDescription = "Equalizador",
                    tint = Color(0xffa8a8c7)
                )
            }
        }

        // Music carousel
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.1f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 25.dp, bottomEnd = 25.dp))
                    .background(Color(0xFF534e80))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {}

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(25))
                    .padding(horizontal = 20.dp)
                    .background(Color(0xff534e80))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(100.dp),
                    painter = painterResource(R.drawable.music_file),
                    contentDescription = "Configurações",
                    tint = Color(0xffa8a8c7)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.8f)
                    .clip(RoundedCornerShape(topStart = 25.dp, bottomStart = 25.dp, topEnd = 0.dp, bottomEnd = 0.dp))
                    .background(Color(0xff534e80))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {}
        }

        Spacer(modifier = Modifier.weight(1f))

        Column (Modifier.fillMaxHeight(0.3f)) {
            Text(
                text = playbackState.currentTrack?.let { uri ->
                    getFileNameFromUri(uri).replace(".mp3", "")
                } ?: "Nenhuma música selecionada",
                color = Color.White,
                fontSize = 25.sp,
            )
            Text(
                text = "<unknown>",
                color = Color.White,
                fontSize = 20.sp,
            )
        }

        Column (Modifier.fillMaxHeight(0.5f)) {
            // Barra de progresso
            Slider(
                modifier = Modifier.fillMaxWidth(1f),
                value = playbackState.currentPosition.toFloat(),
                onValueChange = { musicPlayerViewModel.seekTo(it.toInt()) },
                valueRange = 0f..playbackState.duration.toFloat(),
                colors = SliderDefaults.colors(Color(0xff775ddd)),
            )

            // Tempo decorrido/total
            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(playbackState.currentPosition),
                    color = Color.White,
                    fontSize = 25.sp,
                )

                Text(
                    text = formatTime(playbackState.duration),
                    color = Color.White,
                    fontSize = 25.sp,
                )
            }
        }

        // Media controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { musicPlayerViewModel.skipPrevious() },
                enabled = playbackState.playlistSize > 0
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Anterior",
                    tint = Color(0xffa8a8c7),
                    modifier = Modifier.size(40.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xff6a52c7))
                    .clickable { if (playbackState.isPlaying) musicPlayerViewModel.pause() else musicPlayerViewModel.play() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color(0xffa8a8c7),
                    modifier = Modifier.size(56.dp)
                )
            }

            IconButton(
                onClick = { musicPlayerViewModel.skipNext() },
                enabled = playbackState.playlistSize > 0
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Próxima",
                    tint = Color(0xffa8a8c7),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
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
                    HorizontalDivider()
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

private fun getFileNameFromUri(uri: Uri): String {
    return uri.lastPathSegment?.substringAfterLast('/') ?: "Arquivo desconhecido"
}
