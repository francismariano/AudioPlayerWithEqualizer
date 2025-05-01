package me.francis.audioplayerwithequalizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.francis.audioplayerwithequalizer.ui.theme.AudioPlayerWithEqualizerTheme
import me.francis.audioplayerwithequalizer.viewModels.MusicPlayerViewModel
import me.francis.audioplayerwithequalizer.views.PlayerScreen

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MusicPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel = ViewModelProvider(this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MusicPlayerViewModel(application) as T
                }
            }
        )[MusicPlayerViewModel::class.java]

        setContent {
            AudioPlayerWithEqualizerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerScreen(viewModel = viewModel)
                }
            }
        }
    }
}
