package me.francis.audioplayerwithequalizer.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class PermissionManager(
    private val context: Context,
    private val permissionLauncher: ActivityResultLauncher<String>,
    private val onPermissionGranted: () -> Unit
) {
    fun checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onPermissionGranted()
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }
        } else {
            // Android 12 ou menor
            onPermissionGranted()
        }
    }
}
