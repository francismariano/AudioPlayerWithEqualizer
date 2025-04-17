package me.francis.audioplayerwithequalizer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import me.francis.audioplayerwithequalizer.navigation.NavManager
import me.francis.audioplayerwithequalizer.services.AudioService
import me.francis.audioplayerwithequalizer.ui.theme.AudioPlayerWithEqualizerTheme
import me.francis.audioplayerwithequalizer.viewModels.PlayerViewModel
import me.francis.notificationmodule.NotificationService
import java.io.File

class MainActivity : ComponentActivity() {

    private val playerViewModel = PlayerViewModel()

    @OptIn(androidx.media3.common.util.UnstableApi::class)
    private var audioService: AudioService? = null
    private var serviceConnected = false

    private var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.AudioBinder
            audioService = binder.getService()
            serviceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            serviceConnected = false
        }
    }

    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var file: File
        this.assets.open("sample2.mp3").use { input ->
            file = File(this.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "sample2.mp3")
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val intent = Intent(this, NotificationService::class.java).apply {
            action = "PLAY"
            putExtra("path", "sample2.mp3") // Passa o nome do arquivo como uma String
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        setContent {
            AudioPlayerWithEqualizerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // view model

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
