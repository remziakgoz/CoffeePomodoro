package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun StartButton(
    modifier: Modifier = Modifier, 
    onClick: () -> Unit,
    isRunning: Boolean = false
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("coffeeButton.json"))
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isAnimating) return@LaunchedEffect
        
        isAnimating = true
        
        if (isRunning) {
            // First click: animate from 0f to 0.5f (30 frames)
            val startProgress = 0f
            val targetProgress = 0.5f
            val animationDuration = 1000L
            val frameTime = 16L
            val totalFrames = (animationDuration / frameTime).toInt()
            
            for (frame in 0..totalFrames) {
                val animationProgress = frame.toFloat() / totalFrames
                progress = startProgress + (targetProgress - startProgress) * animationProgress
                delay(frameTime)
            }
            progress = targetProgress
        } else {
            // Second click: animate from 0.5f to 1f (remaining frames)
            val startProgress = 0.5f
            val targetProgress = 1f
            val animationDuration = 1000L
            val frameTime = 16L
            val totalFrames = (animationDuration / frameTime).toInt()
            
            for (frame in 0..totalFrames) {
                val animationProgress = frame.toFloat() / totalFrames
                progress = startProgress + (targetProgress - startProgress) * animationProgress
                delay(frameTime)
            }
            progress = targetProgress
            
            // Reset to beginning for next cycle
            progress = 0f
        }
        
        isAnimating = false
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .size(300.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, 
                indication = null
            ) { 
                val currentTime = System.currentTimeMillis()
                // Debounce with 800ms delay to prevent rapid clicks
                if (currentTime - lastClickTime >= 800L && !isAnimating) {
                    onClick()
                    lastClickTime = currentTime
                }
                // Ignore rapid successive clicks
            }
    )
}