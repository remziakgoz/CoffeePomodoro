package com.remziakgoz.coffeepomodoro

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.remziakgoz.coffeepomodoro.presentation.auth.views.SignInScreen
import com.remziakgoz.coffeepomodoro.presentation.auth.views.SignUpScreen
import com.remziakgoz.coffeepomodoro.presentation.auth.views.WelcomeScreen
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.views.PomodoroScreen
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CoffePomodroTheme
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import com.remziakgoz.coffeepomodoro.presentation.dashboard.views.DashboardScreen
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.PomodoroViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setContent {
            CoffePomodroTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "pomodoro",
                        route = "root"
                    ) {
                        composable("pomodoro") {
                            val parentEntry = remember(navController.currentBackStackEntry) {
                                navController.getBackStackEntry("root")
                            }
                            val viewModel: PomodoroViewModel = hiltViewModel(parentEntry)
                            PomodoroScreen(
                                modifier = Modifier,
                                innerPadding = innerPadding,
                                viewModel = viewModel,
                                onNavigateToProfile = {
                                    val hasSeenWelcome = sharedPreferences.getBoolean("has_seen_welcome", false)
                                    if (hasSeenWelcome) {
                                        navController.navigate("signin")
                                    } else {
                                        navController.navigate("welcome")
                                    }
                                },
                                onSwipeToDashboard = {
                                    navController.navigate("dashboard") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable("welcome") {
                            WelcomeScreen(
                                modifier = Modifier,
                                innerPadding = innerPadding,
                                onContinue = {
                                    // Mark welcome screen as seen
                                    sharedPreferences.edit { putBoolean("has_seen_welcome", true) }
                                    navController.navigate("signin") {
                                        popUpTo("pomodoro") { inclusive = false }
                                    }
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
                        composable("dashboard") {
                            val parentEntry = remember(navController.currentBackStackEntry) {
                                navController.getBackStackEntry("root")
                            }
                            val viewModel: PomodoroViewModel = hiltViewModel(parentEntry)
                            DashboardScreen(
                                modifier = Modifier,
                                viewModel = viewModel,
                                onSwipeToPomodoroScreen = {
                                    navController.navigate("pomodoro") {
                                        popUpTo("pomodoro") { inclusive = true}
                                        launchSingleTop = true
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


