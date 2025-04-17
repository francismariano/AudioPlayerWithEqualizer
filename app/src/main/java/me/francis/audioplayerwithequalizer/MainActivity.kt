package me.francis.audioplayerwithequalizer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import me.francis.audioplayerwithequalizer.navigation.NavManager
import me.francis.audioplayerwithequalizer.services.AudioService
import me.francis.audioplayerwithequalizer.ui.theme.AudioPlayerWithEqualizerTheme
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModel

class MainActivity : ComponentActivity() {

    private val playerViewModel = PlayerViewModel()

    private var audioService: AudioService? = null
    private var serviceConnected = false

    private var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.AudioBinder
            audioService = binder.getService()
            playerViewModel.setAudioService(audioService!!)
            serviceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            serviceConnected = false
        }
    }

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
                    // navigation
                    NavManager(playerViewModel = playerViewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
        val intent = Intent(this, AudioService::class.java)
        playerViewModel.startAudioService(intent, 0, 0) // ver quais flags e id passar
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
        // Desvincula o serviço se estiver conectado
        if (serviceConnected) {
            unbindService(connection)
            serviceConnected = false
        }
    }
}
