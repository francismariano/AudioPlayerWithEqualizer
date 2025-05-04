package me.francis.audioplayerwithequalizer

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.francis.audioplayerwithequalizer.navigation.NavManager
import me.francis.audioplayerwithequalizer.permissions.PermissionState
import me.francis.audioplayerwithequalizer.permissions.PermissionsHandler
import me.francis.audioplayerwithequalizer.ui.theme.AudioPlayerWithEqualizerTheme
import me.francis.audioplayerwithequalizer.viewModels.EqualizerViewModel
import me.francis.audioplayerwithequalizer.viewModels.MusicPlayerController
import me.francis.audioplayerwithequalizer.viewModels.MusicPlayerViewModel

class MainActivity : ComponentActivity() {
    private lateinit var playerController: MusicPlayerController
    private lateinit var musicPlayerViewModel: MusicPlayerViewModel
    private lateinit var equalizerViewModel: EqualizerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        playerController = MusicPlayerController(applicationContext)

        musicPlayerViewModel = ViewModelProvider(
            this,
            MusicPlayerViewModelFactory(application, playerController)
        )[MusicPlayerViewModel::class.java]

        equalizerViewModel = ViewModelProvider(
            this,
            EqualizerViewModelFactory(application, playerController)
        )[EqualizerViewModel::class.java]

        setContent {
            AudioPlayerWithEqualizerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermissionAwareApp(
                        equalizerViewModel = equalizerViewModel,
                        musicPlayerViewModel = musicPlayerViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionAwareApp(
    equalizerViewModel: EqualizerViewModel,
    musicPlayerViewModel: MusicPlayerViewModel
) {

    var currentPermission by remember { mutableStateOf("") }
    var permissionState by remember { mutableStateOf<PermissionState>(PermissionState.Loading) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState = if (isGranted) {
            PermissionState.Granted
        } else {
            PermissionState.Denied(listOf(currentPermission))
        }
    }

    PermissionsHandler(
        onAllPermissionsGranted = { permissionState = PermissionState.Granted },
        onPermissionsDenied = { denied -> permissionState = PermissionState.Denied(denied) }
    )

    when (permissionState) {
        is PermissionState.Loading -> {
            // Mostra uma tela de carregamento enquanto permissões são processadas
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is PermissionState.Granted -> {
            // Agora sim, todas permissões concedidas, pode navegar
            NavManager(
                equalizerViewModel = equalizerViewModel,
                musicPlayerViewModel = musicPlayerViewModel
            )
        }

        is PermissionState.Denied -> {
            val context = LocalContext.current
            val activity = context as? Activity
            val deniedList = (permissionState as PermissionState.Denied).denied
            currentPermission = deniedList.first() // pega a primeira negada

            val message = when (currentPermission) {
                android.Manifest.permission.READ_MEDIA_AUDIO -> "Permissão de mídia de áudio negada"
                android.Manifest.permission.POST_NOTIFICATIONS -> "Permissão de notificações negada"
                else -> "Alguma permissão necessária foi negada"
            }

            AlertDialog(
                onDismissRequest = {},
                title = { Text("Permissão necessária") },
                text = { Text(message) },
                confirmButton = {
                    TextButton(onClick = {
                        val canAskAgain = activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                it,
                                currentPermission
                            )
                        } == true

                        if (canAskAgain) {
                            // Solicita novamente a permissão
                            permissionLauncher.launch(currentPermission)
                        } else {
                            // Abre as configurações do app
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        }

                        // Reseta estado para aguardar novo resultado
                        permissionState = PermissionState.Loading
                    }) {
                        Text("Permitir")
                    }
                }
            )
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

class EqualizerViewModelFactory(
    private val application: Application,
    private val playerController: MusicPlayerController,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EqualizerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EqualizerViewModel(
                application,
                playerController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
