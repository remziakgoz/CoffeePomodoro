package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun FlipScreenContainerWithManualFlip(
    frontScreen: @Composable () -> Unit,
    backScreen: @Composable () -> Unit,
    isFlipped: Boolean,
    onFlipRequested: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableStateOf(0f) }
    val rotation = remember { Animatable(if (isFlipped) 180f else 0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    // Sync with external isFlipped state
    LaunchedEffect(isFlipped) {
        if (!rotation.isRunning) {
            rotation.animateTo(
                targetValue = if (isFlipped) 180f else 0f,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    // Convert drag delta to rotation (more sensitive)
                    val rotationDelta = delta / density.density / 3f // Adjust sensitivity
                    val currentRotation = rotation.value
                    val newRotation = (currentRotation + rotationDelta).coerceIn(-30f, 210f)
                    
                    // Update rotation in real-time during drag
                    coroutineScope.launch {
                        rotation.snapTo(newRotation)
                    }
                    
                    dragOffset += delta
                },
                onDragStopped = {
                    coroutineScope.launch {
                        val currentRotation = rotation.value
                        val threshold = 90f // Half flip point
                        
                        when {
                            // Complete flip to back (Dashboard)
                            currentRotation > threshold && !isFlipped -> {
                                rotation.animateTo(
                                    targetValue = 180f,
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                )
                                onFlipRequested(true)
                            }
                            // Complete flip to front (Pomodoro)  
                            currentRotation < threshold && isFlipped -> {
                                rotation.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                )
                                onFlipRequested(false)
                            }
                            // Bounce back to current state
                            else -> {
                                rotation.animateTo(
                                    targetValue = if (isFlipped) 180f else 0f,
                                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                                )
                            }
                        }
                        dragOffset = 0f
                    }
                }
            )
    ) {
        // 3D Flip Effect - Real-time rotation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 12f * 16.dp.value
                }
        ) {
            if (rotation.value <= 90f) {
                // Front Screen (Pomodoro)
                frontScreen()
            } else {
                // Back Screen (Dashboard) - Mirror fix
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        }
                ) {
                    backScreen()
                }
            }
        }
    }
}

// Simplified version with even more direct control
@Composable
fun FlipScreenContainer(
    frontScreen: @Composable () -> Unit,
    backScreen: @Composable () -> Unit,
    onNavigateToBack: () -> Unit = {},
    onNavigateToFront: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }
    val rotation = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    // Real-time rotation during drag
                    val rotationDelta = delta / density.density / 2.5f
                    val currentRotation = rotation.value
                    val newRotation = (currentRotation + rotationDelta).coerceIn(-20f, 200f)
                    
                    coroutineScope.launch {
                        rotation.snapTo(newRotation)
                    }
                    
                    dragOffset += delta
                },
                onDragStopped = {
                    coroutineScope.launch {
                        val currentRotation = rotation.value
                        val flipThreshold = 90f
                        
                        when {
                            // Flip to Dashboard (back)
                            currentRotation > flipThreshold && !isFlipped -> {
                                rotation.animateTo(180f, tween(300))
                                isFlipped = true
                                onNavigateToBack()
                            }
                            // Flip to Pomodoro (front)
                            currentRotation < flipThreshold && isFlipped -> {
                                rotation.animateTo(0f, tween(300))
                                isFlipped = false
                                onNavigateToFront()
                            }
                            // Bounce back
                            !isFlipped -> {
                                rotation.animateTo(0f, tween(250))
                            }
                            isFlipped -> {
                                rotation.animateTo(180f, tween(250))
                            }
                        }
                        dragOffset = 0f
                    }
                }
            )
    ) {
        // 3D Card Flip
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 12f * 16.dp.value
                }
        ) {
            if (rotation.value <= 90f) {
                // Front (Pomodoro)
                frontScreen()
            } else {
                // Back (Dashboard) 
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        }
                ) {
                    backScreen()
                }
            }
        }
    }
} 