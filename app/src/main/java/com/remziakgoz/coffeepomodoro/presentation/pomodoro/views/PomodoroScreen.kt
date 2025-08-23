package com.remziakgoz.coffeepomodoro.presentation.pomodoro.views

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remziakgoz.coffeepomodoro.presentation.auth.AuthViewModel
import com.remziakgoz.coffeepomodoro.presentation.components.CoffeeAnimation
import com.remziakgoz.coffeepomodoro.presentation.components.CoffeeCoreButton
import com.remziakgoz.coffeepomodoro.presentation.components.CoffeeMachineAnimation
import com.remziakgoz.coffeepomodoro.presentation.components.LogoutDialog
import com.remziakgoz.coffeepomodoro.presentation.components.NextStepButton
import com.remziakgoz.coffeepomodoro.presentation.components.PomodoroWithCanvasClock
import com.remziakgoz.coffeepomodoro.presentation.components.RestartButton
import com.remziakgoz.coffeepomodoro.presentation.components.StartButton
import com.remziakgoz.coffeepomodoro.presentation.init.AppInitViewModel
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.PomodoroState
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.PomodoroViewModel

@Composable
fun PomodoroScreen(
    modifier: Modifier,
    innerPadding: PaddingValues,
    onNavigateToSignIn: () -> Unit = {},
    isProfileButtonEnabled: Boolean = true,
    viewModel: PomodoroViewModel = hiltViewModel(),
    appInitViewModel: AppInitViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    var restartCounter by remember { mutableIntStateOf(0) }
    var showLogOutDialog by remember { mutableStateOf(false) }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isLoggingOut by authViewModel.isLoggingOut.collectAsState()
    val context = LocalContext.current
    val isLandscape = isLandscape()

    LaunchedEffect(Unit) {
        appInitViewModel.syncEverything()
    }

    // Initialize screen state when screen opens
    LaunchedEffect(Unit) {
        viewModel.initializeScreenState()
    }

    // Close logout dialog when logout completes
    LaunchedEffect(isLoggingOut) {
        if (!isLoggingOut && showLogOutDialog) {
            showLogOutDialog = false
            Toast.makeText(context, "Logged out", Toast.LENGTH_LONG).show()
        }
    }

    // Define colors based on current state
    val backgroundColors = when (uiState.value.currentState) {
        PomodoroState.Work, PomodoroState.Paused -> {
            // Warm colors for work/focus (red/orange tones)
            Pair(
                Color(0xFFB85450), // Warm red
                Color(0xFFD4726A)  // Lighter warm red
            )
        }
        PomodoroState.ShortBreak, PomodoroState.LongBreak -> {
            // Cool colors for breaks (green/teal tones)
            Pair(
                Color(0xFF4A8B8A), // Teal green
                Color(0xFF6BA6A5)  // Lighter teal
            )
        }
        PomodoroState.Ready -> {
            // Neutral colors for ready state
            Pair(
                Color(0xFFB85450), // Neutral gray -> Old 0xFF6D6D6D
                Color(0xFFD4726A)  // Lighter gray -> Old 0xFF8A8A8A
            )
        }
        else -> {
            Pair(
                Color(0xFFB85450), // Default neutral -> Old 0xFF6D6D6D
                Color(0xFFD4726A) //               -> Old 0xFF8A8A8A
            )
        }
    }

    // Animate background colors smoothly
    val topColor by animateColorAsState(
        targetValue = backgroundColors.first,
        animationSpec = tween(durationMillis = 1000), // 1 second smooth transition
        label = "topColor"
    )

    val bottomColor by animateColorAsState(
        targetValue = backgroundColors.second,
        animationSpec = tween(durationMillis = 1000), // 1 second smooth transition
        label = "bottomColor"
    )

    // Animate restart button visibility
    val restartButtonAlpha by animateFloatAsState(
        targetValue = when (uiState.value.currentState) {
            PomodoroState.Ready -> 0f //
            PomodoroState.Work, PomodoroState.Paused -> 1f //
            PomodoroState.ShortBreak, PomodoroState.LongBreak -> {
                if (uiState.value.isRunning) 1f else 0f //
            }
            else -> 0f
        },
        animationSpec = tween(durationMillis = 300),
        label = "restartButtonAlpha"
    )

    val isNextStepEnabled = when (uiState.value.currentState) { // ➕ NEW
        PomodoroState.Ready -> false
        PomodoroState.Work, PomodoroState.Paused -> true
        PomodoroState.ShortBreak, PomodoroState.LongBreak -> uiState.value.isRunning
        else -> false
    }


    val nextStepButtonAlpha by animateFloatAsState(
        targetValue = if (isNextStepEnabled) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "nextStepButtonAlpha"
    )

    // Create gradient background
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(topColor, bottomColor),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            if (isLandscape) {
                // Landscape Layout - New 3-row structure
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Top Row - Controls (FullMaxWidth)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User profile icon - left
                        if (isProfileButtonEnabled) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                    .clickable {
                                        if (isLoggedIn) {
                                            showLogOutDialog = true
                                        } else {
                                            onNavigateToSignIn()
                                        }
                                    }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = if (isLoggedIn) "User Profile - Logout" else "Sign In",
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(40.dp))
                        }

                        // Cycle count - center
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Color.Black.copy(alpha = 0.7f)
                                )
                                .clickable { showResetDialog = true }
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${uiState.value.cycleCount + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }

                        // Restart button - right
                        RestartButton(
                            modifier = Modifier
                                .alpha(restartButtonAlpha)
                                .size(40.dp),
                            onClick = {
                                viewModel.restart()
                                restartCounter++
                            }
                        )
                    }

                    // Middle Row - Clock, Button, Coffee (Left to Right)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth().
                                padding(horizontal = 10.dp)
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Digital Clock - Left
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            PomodoroWithCanvasClock(
                                remainingTime = uiState.value.remainingTime,
                                fontSize = 120.sp
                            )
                        }

                        // Start Button - Center
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            when (uiState.value.currentState) {
                                PomodoroState.Ready -> {
                                    StartButton(
                                        modifier = Modifier.size(260.dp),
                                        onClick = { viewModel.toggleTimer() },
                                        isRunning = uiState.value.isRunning
                                    )
                                }

                                PomodoroState.Work -> {
                                    StartButton(
                                        modifier = Modifier.size(260.dp),
                                        onClick = { viewModel.toggleTimer() },
                                        isRunning = uiState.value.isRunning
                                    )
                                }

                                PomodoroState.Paused -> {
                                    StartButton(
                                        modifier = Modifier.size(260.dp),
                                        onClick = { viewModel.toggleTimer() },
                                        isRunning = uiState.value.isRunning
                                    )
                                }

                                PomodoroState.ShortBreak -> {
                                    CoffeeCoreButton(
                                        modifier = Modifier.size(200.dp),
                                        onClick = { viewModel.toggleTimer() },
                                        lottieAssetName = "coffeeTurned.json"
                                    )
                                }

                                PomodoroState.LongBreak -> {
                                    CoffeeCoreButton(
                                        modifier = Modifier.size(200.dp),
                                        onClick = { viewModel.toggleTimer() },
                                        lottieAssetName = "coreButtonForLongBreak.json"
                                    )
                                }

                                else -> {
                                    // Handle other states if needed
                                }
                            }
                        }

                        // Coffee Animation - Right
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            when (uiState.value.currentState) {
                                PomodoroState.LongBreak -> {
                                    CoffeeMachineAnimation(
                                        modifier = modifier.size(700.dp),
                                        innerPadding = innerPadding,
                                        shouldPlay = uiState.value.isRunning,
                                        shouldRestart = restartCounter > 0,
                                        onRestartConsumed = { restartCounter = 0 },
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                else ->
                                    CoffeeAnimation(
                                        modifier = modifier.size(1200.dp),
                                        innerPadding = innerPadding,
                                        animationProgress = uiState.value.animationProgress,
                                        contentScale = ContentScale.Crop
                                    )
                            }
                        }
                    }

                    // Bottom Row - Next Step Button (FullMaxWidth Center)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NextStepButton(
                            modifier = Modifier.alpha(nextStepButtonAlpha),
                            onClick = { viewModel.nextStep() },
                            enabled = isNextStepEnabled
                        )
                    }
                }
            } else {
                // Portrait Layout - Original vertical arrangement
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        // User profile icon in top left - aligned with cycle indicator
                        if (isProfileButtonEnabled) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                    .clickable {
                                        if (isLoggedIn) {
                                            showLogOutDialog = true
                                        } else {
                                            onNavigateToSignIn()
                                        }
                                    }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = if (isLoggedIn) "User Profile - Logout" else "Sign In",
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Color.Black.copy(alpha = 0.7f)
                                )
                                .clickable { showResetDialog = true }
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${uiState.value.cycleCount + 1}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }

                        // Restart button in top right - absolute positioning
                        RestartButton(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .alpha(restartButtonAlpha),
                            onClick = {
                                viewModel.restart()
                                restartCounter++
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Show the digital clock with current remaining time
                    PomodoroWithCanvasClock(remainingTime = uiState.value.remainingTime)

                    // Coffee animation with timer progress and Coffee machine animation when in long break
                    when (uiState.value.currentState) {
                        PomodoroState.LongBreak -> {
                            CoffeeMachineAnimation(
                                modifier = modifier.size(400.dp),
                                innerPadding = innerPadding,
                                shouldPlay = uiState.value.isRunning,
                                shouldRestart = restartCounter > 0,
                                onRestartConsumed = { restartCounter = 0 },
                            )
                        }

                        else ->
                            CoffeeAnimation(
                                modifier = modifier.size(400.dp),
                                innerPadding = innerPadding,
                                animationProgress = uiState.value.animationProgress
                            )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Button layout with start/pause button perfectly centered and next step button on the right
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Main button perfectly centered
                        when (uiState.value.currentState) {
                            PomodoroState.Ready -> {
                                StartButton(
                                    modifier = Modifier.size(300.dp),
                                    onClick = { viewModel.toggleTimer() },
                                    isRunning = uiState.value.isRunning
                                )
                            }

                            PomodoroState.Work -> {
                                StartButton(
                                    modifier = Modifier.size(300.dp),
                                    onClick = { viewModel.toggleTimer() },
                                    isRunning = uiState.value.isRunning
                                )
                            }

                            PomodoroState.Paused -> {
                                StartButton(
                                    modifier = Modifier.size(300.dp),
                                    onClick = { viewModel.toggleTimer() },
                                    isRunning = uiState.value.isRunning
                                )
                            }

                            PomodoroState.ShortBreak -> {
                                CoffeeCoreButton(
                                    modifier = Modifier.size(300.dp),
                                    onClick = { viewModel.toggleTimer() },
                                    lottieAssetName = "coffeeTurned.json"
                                )
                            }

                            PomodoroState.LongBreak -> {
                                CoffeeCoreButton(
                                    modifier = Modifier.size(300.dp),
                                    onClick = { viewModel.toggleTimer() },
                                    lottieAssetName = "coreButtonForLongBreak.json"
                                )
                            }

                            else -> {
                                // Handle other states if needed
                            }
                        }

                        // Next step button positioned on the right
                        NextStepButton(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 20.dp)
                                .alpha(nextStepButtonAlpha),
                            onClick = { viewModel.nextStep() },
                            enabled = isNextStepEnabled
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

            // Reset confirmation dialog
            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = {
                        Text(
                            text = "Reset Pomodoro Count",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Text(
                            text = "Do you want to refresh the pomodoro count?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.resetEverything()
                                showResetDialog = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showResetDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
            LogoutDialog(
                show = showLogOutDialog,
                userEmail = uiState.value.stats.email,
                isLoggingOut = isLoggingOut,
                onConfirmLogout = {
                    authViewModel.logout()
                    // Don't close dialog immediately - let logout complete first
                },
                onDismiss = {showLogOutDialog = false}
            )
        }
    }
}