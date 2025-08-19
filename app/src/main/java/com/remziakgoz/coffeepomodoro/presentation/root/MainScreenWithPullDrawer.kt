package com.remziakgoz.coffeepomodoro.presentation.root

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.remziakgoz.coffeepomodoro.presentation.dashboard.DashboardViewModel
import com.remziakgoz.coffeepomodoro.presentation.dashboard.views.DashboardScreen
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.PomodoroViewModel
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.views.PomodoroScreen
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun MainScreenWithPullDrawer(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    pomodoroViewModel: PomodoroViewModel,
    dashboardViewModel: DashboardViewModel,
    onNavigateToProfile: () -> Unit = {}
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    val closedX = 0f
    val openX = -screenWidthPx

    val openThreshold = screenWidthPx * 0.35f
    val velocityThreshold = 1800f // px/s

    val offsetX = remember { Animatable(closedX) }
    val isOpen by remember {
        derivedStateOf { offsetX.targetValue == openX || offsetX.value < (openX / 2f) }
    }

    val scope = rememberCoroutineScope()

    suspend fun animateTo(target: Float) {
        offsetX.animateTo(
            targetValue = target,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    val new = (offsetX.value + delta)
                        .coerceIn(openX * 1.05f, closedX)
                    scope.launch { offsetX.snapTo(new) }
                },
                onDragStopped = { velocity ->
                    val shouldOpen = when {
                        velocity < -velocityThreshold -> true
                        velocity > velocityThreshold -> false
                        else -> abs(offsetX.value - closedX) > openThreshold
                    }
                    scope.launch { if (shouldOpen) animateTo(openX) else animateTo(closedX) }
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
        ) {
            PomodoroScreen(
                modifier = Modifier,
                innerPadding = innerPadding,
                onNavigateToProfile = onNavigateToProfile,
                viewModel = pomodoroViewModel
            )
        }

        val scrimAlpha = ((closedX - offsetX.value) / (closedX - openX))
            .coerceIn(0f, 1f) * 0.5f

        if (scrimAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        scope.launch { animateTo(closedX) }
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    val dash = (screenWidthPx + offsetX.value).coerceAtLeast(0f)
                    IntOffset(dash.roundToInt(), 0)
                }
        ) {
            DashboardScreen(
                modifier = Modifier,
                dashboardViewModel = dashboardViewModel,
                onSwipeToPomodoroScreen = { scope.launch { animateTo(closedX) } }
            )
        }

        if (isOpen) {
            val minDp = 24.dp
            val maxDp = 64.dp
            val tenPercentDp = with(density) { (screenWidthPx * 0.1f).toDp() }
            val dragAreaWidth = tenPercentDp.coerceIn(minDp, maxDp)

            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .width(dragAreaWidth)
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            if (delta > 0) {
                                val new = (offsetX.value + delta).coerceIn(openX, closedX)
                                scope.launch { offsetX.snapTo(new) }
                            }
                        },
                        onDragStopped = { velocity ->
                            scope.launch {
                                if (velocity > velocityThreshold || (offsetX.value > openX * 0.3f)) {
                                    animateTo(closedX)
                                } else {
                                    animateTo(openX)
                                }
                            }
                        }
                    )
            )
        }
    }
}
