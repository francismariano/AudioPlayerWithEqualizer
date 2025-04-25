package me.francis.audioplayerwithequalizer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.DocumentsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import me.francis.audioplayerwithequalizer.navigation.NavManager
import me.francis.audioplayerwithequalizer.services.AudioService
import me.francis.audioplayerwithequalizer.services.Musica
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

    // Launcher para pedir permissão de leitura de áudio
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            abrirSeletorDeDiretorio()
        } else {
            println("**-- Permissão negada para READ_MEDIA_AUDIO")
        }
    }

    // Launcher para selecionar diretório
    private val directoryPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            processarArquivosDeAudio(it)
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

        verificarPermissaoAudio()
    }

    private fun verificarPermissaoAudio() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    abrirSeletorDeDiretorio()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_AUDIO) -> {
                    // Aqui você pode mostrar um diálogo explicando por que precisa
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_AUDIO)
                }

                else -> {
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_AUDIO)
                }
            }
        } else {
            // Se for Android abaixo do 13
            abrirSeletorDeDiretorio()
        }
    }

    private fun abrirSeletorDeDiretorio() {
        directoryPickerLauncher.launch(null)
    }

    private fun processarArquivosDeAudio(uri: Uri) {
        val musicaList = mutableListOf<Musica>()
        var index = 0

        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri,
            DocumentsContract.getTreeDocumentId(uri)
        )

        val cursor = contentResolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
            ),
            null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val documentId = it.getString(0)
                val nome = it.getString(1)
                val mimeType = it.getString(2)

                if (mimeType.startsWith("audio/")) {
                    val fileUri = DocumentsContract.buildDocumentUriUsingTree(uri, documentId)
                    val path = fileUri.toString()
                    musicaList.add(Musica(index++, nome, path))
                }
            }
        }

        // Imprimir no Logcat
        musicaList.forEach {
            println("**-- Index: ${it.index}, Nome: ${it.nome}, Path: ${it.path}")
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
