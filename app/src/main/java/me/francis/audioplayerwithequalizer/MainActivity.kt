package me.francis.audioplayerwithequalizer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import me.francis.audioplayerwithequalizer.navigation.NavManager
import me.francis.audioplayerwithequalizer.services.AudioServiceRepository
import me.francis.audioplayerwithequalizer.services.AudioServiceRepositoryImpl
import me.francis.audioplayerwithequalizer.ui.theme.AudioPlayerWithEqualizerTheme
import me.francis.audioplayerwithequalizer.viewModels.EqualizerViewModel
import me.francis.audioplayerwithequalizer.viewModels.EqualizerViewModelFactory
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModel
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModelFactory

class MainActivity : ComponentActivity() {

    private val audioServiceRepositoryImpl = AudioServiceRepositoryImpl()
    private val audioServiceRepository by lazy {
        AudioServiceRepository(
            application,
            audioServiceRepositoryImpl
        )
    }

    private val playerViewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(audioServiceRepository)
    }

    private val equalizerViewModel: EqualizerViewModel by viewModels {
        EqualizerViewModelFactory(audioServiceRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioPlayerWithEqualizerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavManager(
                        equalizerViewModel = equalizerViewModel,
                        playerViewModel = playerViewModel
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
        val intent = Intent(this, AudioServiceRepositoryImpl::class.java)
        this.startService(intent)
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
        val intent = Intent(this, AudioServiceRepositoryImpl::class.java)
        this.stopService(intent)
    }
}
