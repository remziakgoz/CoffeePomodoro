package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun CoffeeCoreButton(modifier: Modifier = Modifier, onClick: () -> Unit, lottieAssetName: String) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieAssetName))
    val animatable = rememberLottieAnimatable()
    var lastClickTime by remember { mutableLongStateOf(0L) }

    // Debounce delay in milliseconds
    val debounceDelay = 1000L // 1 second

    LaunchedEffect(lastClickTime) {
        if (lastClickTime > 0 && composition != null) {
            animatable.animate(
                composition = composition
            )
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { animatable.progress },
        modifier = modifier
            .size(300.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                val currentTime = System.currentTimeMillis()
                
                // Only process click if enough time has passed since last click
                if (currentTime - lastClickTime >= debounceDelay) {
                    lastClickTime = currentTime
                onClick()
                }
                // Ignore rapid successive clicks
            }
    )
}