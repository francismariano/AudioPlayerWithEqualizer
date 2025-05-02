package me.francis.audioplayerwithequalizer.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

@Composable
fun NotificationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        Log.d("NotificationPermissionHandler", "LaunchedEffect called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("NotificationPermissionHandler", "Checking notification permission")
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    Log.d("NotificationPermissionHandler", "Permission already granted")
                    onPermissionGranted()
                }

                else -> {
                    Log.d("NotificationPermissionHandler", "Requesting notification permission")
                    activity?.let {
                        val shouldShowRationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                it,
                                Manifest.permission.POST_NOTIFICATIONS
                            )

                        if (shouldShowRationale) {
                            onPermissionDenied()
                        } else {
                            ActivityCompat.requestPermissions(
                                it,
                                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                NOTIFICATION_PERMISSION_REQUEST_CODE
                            )
                        }
                    }
                }
            }
        } else {
            Log.d("NotificationPermissionHandler", "Permission already granted")
            onPermissionGranted()
        }
    }
}