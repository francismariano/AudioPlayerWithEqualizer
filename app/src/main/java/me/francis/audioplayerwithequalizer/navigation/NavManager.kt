package me.francis.audioplayerwithequalizer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.francis.audioplayerwithequalizer.viewModels.EqualizerViewModel
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModel
import me.francis.audioplayerwithequalizer.views.EqualizerView
import me.francis.audioplayerwithequalizer.views.MediaPlayerUI

@Composable
internal fun NavManager(
    equalizerViewModel: EqualizerViewModel,
    playerViewModel: PlayerViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "playerView"
    ) {
        composable(route = "playerView") {
            MediaPlayerUI(
                navController = navController,
                playerViewModel = playerViewModel
            )
        }

        // navegação para o equalizador
        composable(route = "equalizer") {
            EqualizerView(
                navController = navController,
                equalizerViewModel = equalizerViewModel,
            )
        }
    }
}
