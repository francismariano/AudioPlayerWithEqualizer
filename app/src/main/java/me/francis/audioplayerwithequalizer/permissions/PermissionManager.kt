package me.francis.audioplayerwithequalizer.permissions

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

sealed class PermissionState {
    object Loading : PermissionState()
    object Granted : PermissionState()
    data class Denied(val denied: List<String>) : PermissionState()
}

@Composable
fun PermissionsHandler(
    onAllPermissionsGranted: () -> Unit,
    onPermissionsDenied: (deniedPermissions: List<String>) -> Unit
) {
    val context = LocalContext.current

    // Permissions to request depending on Android version
    val permissions = remember {
        mutableListOf<String>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(android.Manifest.permission.READ_MEDIA_AUDIO)
                add(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    var permissionsRequested by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val denied = result.filter { !it.value }.keys.toList()
        if (denied.isEmpty()) {
            onAllPermissionsGranted()
        } else {
            onPermissionsDenied(denied)
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionsRequested) {
            val deniedPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }

            if (deniedPermissions.isEmpty()) {
                onAllPermissionsGranted()
            } else {
                launcher.launch(deniedPermissions.toTypedArray())
            }

            permissionsRequested = true
        }
    }
}
