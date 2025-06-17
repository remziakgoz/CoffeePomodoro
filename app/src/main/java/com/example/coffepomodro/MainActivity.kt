package com.example.coffepomodro

import CoffeAnimation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.coffepomodro.presentation.ui.theme.CoffePomodroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffePomodroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CoffeAnimation(innerPadding, true)
                }
            }
        }
    }
}


