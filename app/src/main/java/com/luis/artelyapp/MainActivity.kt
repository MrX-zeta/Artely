package com.luis.artelyapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.luis.artelyapp.ui.theme.ArtelyAppTheme
import com.luis.artelyapp.view.AppContent

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { //isGranted: Boolean ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // DESHABILITADO: La persistencia causa problemas con datos obsoletos en caché
        // Especialmente con el estado isRead de los mensajes
        /*
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            android.util.Log.w("MainActivity", "Error al habilitar la persistencia: ${e.message}")
        }
        */

        android.util.Log.d("MainActivity", "🔥 Persistencia de Firebase DESHABILITADA para evitar caché obsoleta")

        checkAndRequestNotificationPermission()

        setContent {
            ArtelyAppTheme {
                AppContent()
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            val isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}
