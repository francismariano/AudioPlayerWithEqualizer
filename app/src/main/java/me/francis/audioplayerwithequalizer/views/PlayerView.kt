package me.francis.audioplayerwithequalizer.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.francis.audioplayerwithequalizer.R
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModel

@Composable
internal fun MediaPlayerUI(
    songTitle: String,
    isPlaying: Boolean,
    navController: NavController,
    playerViewModel: PlayerViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = songTitle,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) { //playerViewModel::previousTrack) {
                Icon(
                    painter = painterResource(R.drawable.previous),
                    contentDescription = "Faixa anterior",
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = playerViewModel::playPause) {
                Icon(
                    painter = if (isPlaying) painterResource(R.drawable.pause) else painterResource(
                        R.drawable.play
                    ),
                    contentDescription = if (isPlaying) "Pausar" else "Tocar",
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(onClick = {}) { //playerViewModel::nextTrack) {
                Icon(
                    painter = painterResource(R.drawable.next),
                    contentDescription = "Próxima faixa",
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = { navController.navigate("equalizer") }) {
                Icon(
                    painter = painterResource(R.drawable.equalizer),
                    contentDescription = "Próxima faixa",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
