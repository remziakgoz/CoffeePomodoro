package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun CoffeeMachineAnimation(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    shouldPlay: Boolean,
    shouldRestart: Boolean = false,
    onRestartConsumed: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("coffeeBrownPink.json"))
    val animatable = rememberLottieAnimatable()
    var lastPlayTime by remember { mutableLongStateOf(0L) }
    var isInitialDone by remember { mutableStateOf(false) }
    var savedProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(shouldRestart) {
        if (shouldRestart) {
            lastPlayTime = 0L
            isInitialDone = false
            savedProgress = 0f
            if (composition != null) {
                animatable.snapTo(composition, 0f)
            }
            onRestartConsumed()
        }
    }

    LaunchedEffect(lastPlayTime) {
        if (composition != null) {
            if (!isInitialDone) {
                animatable.animate(
                    composition = composition,
                    clipSpec = LottieClipSpec.Frame(0, 160),
                    iterations = 1
                )
                isInitialDone = true
                savedProgress = animatable.progress
            }
            
            if (lastPlayTime > 0) {
                animatable.animate(
                    composition = composition,
                    clipSpec = LottieClipSpec.Frame(160, composition!!.durationFrames.toInt()),
                    iterations = LottieConstants.IterateForever,
                    initialProgress = savedProgress
                )
            }
        }
    }

    LaunchedEffect(shouldPlay) {
        lastPlayTime = if (shouldPlay) {
            System.currentTimeMillis()
        } else {
            savedProgress = animatable.progress
            0L
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { animatable.progress },
            modifier = Modifier.size(400.dp)
        )
    }
}