package me.francis.audioplayerwithequalizer

import android.app.Application
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
import me.francis.audioplayerwithequalizer.viewModels.MusicPlayerController
import me.francis.audioplayerwithequalizer.viewModels.MusicPlayerViewModel
import me.francis.audioplayerwithequalizer.views.PlayerScreen

class MainActivity : ComponentActivity() {
    private lateinit var playerController: MusicPlayerController
    private lateinit var viewModel: MusicPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        playerController = MusicPlayerController(applicationContext)
        viewModel = ViewModelProvider(
            this,
            MusicPlayerViewModelFactory(application, playerController)
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

class MusicPlayerViewModelFactory(
    private val application: Application,
    private val playerController: MusicPlayerController,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicPlayerViewModel(
                application,
                playerController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
