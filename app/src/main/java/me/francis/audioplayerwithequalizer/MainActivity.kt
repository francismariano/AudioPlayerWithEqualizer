package me.francis.audioplayerwithequalizer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow

//import me.francis.playbackmodule.PlaybackModule

class MainActivity : ComponentActivity() {

    // é um StateFlow para atualizar o dado no compose, poderia ser encapsulado em um objeto (viewmodel, por exemplo)
    private val audioService = MutableStateFlow<AudioService?>(null)
    private var isServiceConnected = false

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.AudioBinder
            audioService.value = binder.getService()
            isServiceConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService.value = null
            isServiceConnected = false
            Log.d("MainActivity", "onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AudioPlayerScreen(audioService = audioService)
//                    RawAudioPlayer()
                }
            }
        }
    }

    override fun onStart() {
        Log.d("MainActivity", "onStart")
        super.onStart()
        val intent = Intent(this, AudioService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onResume() {
        Log.d("MainActivity", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("MainActivity", "onStop")
        super.onStop()
        if (isServiceConnected) {
            unbindService(serviceConnection)
            isServiceConnected = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}