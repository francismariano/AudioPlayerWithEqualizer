package me.francis.audioplayerwithequalizer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.francis.audioplayerwithequalizer.viewModels.EqualizerViewModel
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModel
import me.francis.audioplayerwithequalizer.views.EqualizerView
import me.francis.audioplayerwithequalizer.views.MediaPlayerUI

@Composable
internal fun NavManager(
    playerViewModel: PlayerViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "playerView"
    ) {
        composable(route = "playerView") {
            // todo: colocar esses estados na viewModel
            var isPlaying by remember { mutableStateOf(false) }
            var currentSong by remember { mutableStateOf("Nome da Música") }

            MediaPlayerUI(
                songTitle = currentSong,
                isPlaying = isPlaying,
                navController = navController,
                playerViewModel = playerViewModel
            )
        }

        // navegação para o equalizador
        composable(route = "equalizer") {
            EqualizerView(
                navController = navController,
                equalizerViewModel = EqualizerViewModel(),
            )
        }
    }
}
