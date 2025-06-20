package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun StartButton(modifier: Modifier = Modifier, shouldStart: Boolean, onClick: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("coffeeButton.json"))
    val animatable = rememberLottieAnimatable()
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            composition?.let {
                animatable.animate(
                    composition = it, clipSpec = LottieClipSpec.Progress(0f, 0.5f)
                )
            }
        }
        if (!isAnimating) {
            animatable.animate(
                composition = composition, clipSpec = LottieClipSpec.Progress(0.5f, 1f)
            )
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { animatable.progress },
        modifier = modifier
            .size(300.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null
            ) { isAnimating = !isAnimating; onClick() }
    )
}