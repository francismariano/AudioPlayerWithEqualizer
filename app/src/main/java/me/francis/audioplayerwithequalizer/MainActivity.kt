package me.francis.audioplayerwithequalizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import me.francis.audioplayerwithequalizer.navigation.NavManager
import me.francis.audioplayerwithequalizer.ui.theme.AudioPlayerWithEqualizerTheme
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioPlayerWithEqualizerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // view model
                    val playerViewModel = PlayerViewModel(context = LocalContext.current)

                    // navigation
                    NavManager(playerViewModel = playerViewModel)
                }
            }
        }
    }
}
