package com.remziakgoz.coffeepomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.PomodoroViewModel
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.views.PomodoroScreen
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CoffePomodroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffePomodroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PomodoroScreen(modifier = Modifier, innerPadding)
                }
            }
        }
    }
}


