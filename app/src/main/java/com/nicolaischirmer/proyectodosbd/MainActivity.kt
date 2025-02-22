package com.nicolaischirmer.proyectodosbd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nicolaischirmer.proyectodosbd.data.AuthManager
import com.nicolaischirmer.proyectodosbd.ui.navigation.Navegacion
import com.nicolaischirmer.proyectodosbd.ui.theme.ProyectodosbdTheme

class MainActivity : ComponentActivity() {
    val auth = AuthManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            ProyectodosbdTheme {
                Navegacion(auth)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }
}
