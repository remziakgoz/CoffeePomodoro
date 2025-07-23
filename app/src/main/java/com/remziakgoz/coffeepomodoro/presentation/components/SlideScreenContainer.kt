package com.remziakgoz.coffeepomodoro.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SlideScreenContainer(
    frontScreen: @Composable () -> Unit,
    backScreen: @Composable () -> Unit,
    isShowingBack: Boolean,
    onNavigationRequested: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Use BoxWithConstraints to get actual measured width
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()

        // Sync with external state
        LaunchedEffect(isShowingBack) {
            if (!animatedOffsetX.isRunning) {
                animatedOffsetX.animateTo(
                    targetValue = if (isShowingBack) -screenWidthPx else 0f,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds() // Hide overflow content - THIS FIXES THE ISSUE
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        // Restrict gestures based on current screen
                        val allowDrag = when {
                            // Pomodoro ekranında - sadece sağdan sola (Dashboard'a) izin ver
                            !isShowingBack && delta < 0 -> true
                            // Dashboard'da - sadece soldan sağa (Pomodoro'ya) izin ver
                            isShowingBack && delta > 0 -> true
                            // Diğer durumlar - izin verme
                            else -> false
                        }
                        
                        if (allowDrag) {
                            offsetX += delta
                            // Update animation target in real-time
                            coroutineScope.launch {
                                animatedOffsetX.snapTo(
                                    if (isShowingBack) -screenWidthPx + offsetX else offsetX
                                )
                            }
                        }
                    },
                    onDragStopped = { velocity ->
                        coroutineScope.launch {
                            val currentOffset = animatedOffsetX.value
                            val threshold = screenWidthPx * 0.3f // 30% of screen width

                            when {
                                // Swipe left to Dashboard (sağdan sola)
                                currentOffset < -threshold && !isShowingBack -> {
                                    animatedOffsetX.animateTo(
                                        targetValue = -screenWidthPx,
                                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                                    )
                                    onNavigationRequested(true)
                                }
                                // Swipe right to Pomodoro (soldan sağa)  
                                currentOffset > -screenWidthPx + threshold && isShowingBack -> {
                                    animatedOffsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                                    )
                                    onNavigationRequested(false)
                                }
                                // Bounce back to current screen
                                else -> {
                                    animatedOffsetX.animateTo(
                                        targetValue = if (isShowingBack) -screenWidthPx else 0f,
                                        animationSpec = tween(200, easing = FastOutSlowInEasing)
                                    )
                                }
                            }
                            offsetX = 0f
                        }
                    }
                )
        ) {
            // Front Screen (Pomodoro) - Always at position 0
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(animatedOffsetX.value.roundToInt(), 0) }
                    .clipToBounds() // Clip each screen individually
            ) {
                frontScreen()
            }

            // Back Screen (Dashboard) - Starts completely off-screen to the right
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset((screenWidthPx + animatedOffsetX.value).roundToInt(), 0) }
                    .clipToBounds() // Clip each screen individually
            ) {
                backScreen()
            }
        }
    }
}

// Alternative with smoother fade transition
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun SlideScreenContainerWithFade(
    frontScreen: @Composable () -> Unit,
    backScreen: @Composable () -> Unit,
    isShowingBack: Boolean,
    onNavigationRequested: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp.value * 3

    LaunchedEffect(isShowingBack) {
        if (!animatedOffsetX.isRunning) {
            animatedOffsetX.animateTo(
                targetValue = if (isShowingBack) -screenWidth else 0f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offsetX += delta
                    coroutineScope.launch {
                        animatedOffsetX.snapTo(
                            if (isShowingBack) -screenWidth + offsetX else offsetX
                        )
                    }
                },
                onDragStopped = {
                    coroutineScope.launch {
                        val currentOffset = animatedOffsetX.value
                        val threshold = screenWidth * 0.25f

                        when {
                            currentOffset < -threshold && !isShowingBack -> {
                                animatedOffsetX.animateTo(-screenWidth, tween(250))
                                onNavigationRequested(true)
                            }
                            currentOffset > -screenWidth + threshold && isShowingBack -> {
                                animatedOffsetX.animateTo(0f, tween(250))
                                onNavigationRequested(false)
                            }
                            else -> {
                                animatedOffsetX.animateTo(
                                    if (isShowingBack) -screenWidth else 0f,
                                    tween(200)
                                )
                            }
                        }
                        offsetX = 0f
                    }
                }
            )
    ) {
        // Calculate alpha for fade effect
        val progress = abs(animatedOffsetX.value) / screenWidth
        val frontAlpha = (1f - progress).coerceIn(0f, 1f)
        val backAlpha = progress.coerceIn(0f, 1f)

        // Front Screen with fade
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(animatedOffsetX.value.roundToInt(), 0) }
                .graphicsLayer { alpha = frontAlpha }
        ) {
            frontScreen()
        }

        // Back Screen with fade
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((screenWidth + animatedOffsetX.value).roundToInt(), 0) }
                .graphicsLayer { alpha = backAlpha }
        ) {
            backScreen()
        }
    }
} 