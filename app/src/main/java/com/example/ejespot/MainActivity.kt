package com.example.ejespot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ejespot.navigation.AppNavigation
import com.example.ejespot.ui.theme.EjeSpotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjeSpotTheme {
                AppNavigation()
            }
        }
    }
}