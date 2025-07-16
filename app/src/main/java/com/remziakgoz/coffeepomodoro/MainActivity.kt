package com.remziakgoz.coffeepomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.remziakgoz.coffeepomodoro.presentation.auth.views.SignInScreen
import com.remziakgoz.coffeepomodoro.presentation.auth.views.SignUpScreen
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.views.PomodoroScreen
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CoffePomodroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffePomodroTheme {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "pomodoro"
                    ) {
                        composable("pomodoro") {
                            PomodoroScreen(
                                modifier = Modifier,
                                innerPadding = innerPadding,
                                onNavigateToProfile = {
                                    navController.navigate("signin")
                                }
                            )
                        }
                        composable("signin") {
                            SignInScreen(
                                modifier = Modifier,
                                innerPadding = innerPadding,
                                onNavigateBack = {
                                    navController.navigateUp()
                                },
                                onNavigateToSignUp = {
                                    navController.navigate("signup") {
                                        popUpTo("signin") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("signup") {
                            SignUpScreen(
                                modifier = Modifier,
                                innerPadding = innerPadding,
                                onNavigateBack = {
                                    navController.navigate("signin") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                },
                                onNavigateToSignIn = {
                                    navController.navigate("signin") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


