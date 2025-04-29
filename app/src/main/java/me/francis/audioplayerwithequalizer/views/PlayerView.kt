package me.francis.audioplayerwithequalizer.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.francis.audioplayerwithequalizer.R
import me.francis.audioplayerwithequalizer.services.AudioFileManager
import me.francis.audioplayerwithequalizer.services.musicList
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModel

@Composable
internal fun MediaPlayerUI(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    audioFileManager: AudioFileManager,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Nome da música", // songTitle ?: "Nenhuma música tocando",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tempo + Slider
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Slider(
                value = .5f,
                onValueChange = { newValue ->
                    playerViewModel.seekTo(0)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(0))
                Text(formatTime(3))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Controles
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { playerViewModel.skipToPrevious("") }) {
                Icon(
                    painter = painterResource(R.drawable.previous),
                    contentDescription = "Faixa anterior",
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = playerViewModel::play) {
                Icon(
                    painter = painterResource(R.drawable.play),
                    contentDescription = "Tocar",
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(onClick = playerViewModel::pause) {
                Icon(
                    painter = painterResource(R.drawable.pause),
                    contentDescription = "Pausar",
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(onClick = { playerViewModel.skipToNext("") }) {
                Icon(
                    painter = painterResource(R.drawable.next),
                    contentDescription = "Próxima faixa",
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = { navController.navigate("equalizer") }) {
                Icon(
                    painter = painterResource(R.drawable.equalizer),
                    contentDescription = "Equalizador",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Column {
            IconButton(onClick = { audioFileManager.getDirectory() }) {
                Icon(
                    painter = painterResource(R.drawable.equalizer),
                    contentDescription = "Trocar pasta de músicas",
                    modifier = Modifier.size(48.dp)
                )
            }

            LazyColumn {
                musicList.forEach {
                    item {
                        Text(text = "Item: ${it.index}, ${it.nome}")
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
