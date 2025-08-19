package com.remziakgoz.coffeepomodoro

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.remziakgoz.coffeepomodoro.presentation.auth.views.SignInScreen
import com.remziakgoz.coffeepomodoro.presentation.auth.views.SignUpScreen
import com.remziakgoz.coffeepomodoro.presentation.auth.views.WelcomeScreen
import com.remziakgoz.coffeepomodoro.presentation.dashboard.DashboardViewModel
import com.remziakgoz.coffeepomodoro.presentation.dashboard.views.DashboardScreen
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.PomodoroViewModel
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.views.PomodoroScreen
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CoffePomodroTheme
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Color

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.navigationBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.TRANSPARENT

            val controller = ViewCompat.getWindowInsetsController(window.decorView)
            controller?.let {
                it.hide(WindowInsetsCompat.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.navigationBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.TRANSPARENT

            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

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
                            val dashboardViewModel: DashboardViewModel = hiltViewModel(parentEntry)
                            DashboardScreen(
                                modifier = Modifier,
                                dashboardViewModel = dashboardViewModel,
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


