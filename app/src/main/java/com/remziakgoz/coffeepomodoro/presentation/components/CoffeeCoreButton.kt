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
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun CoffeeCoreButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("coreTurn.json"))
    val animatable = rememberLottieAnimatable()
    var isClicked by remember { mutableStateOf(false) }

    LaunchedEffect(isClicked) {
        if (isClicked && composition != null) {
            animatable.animate(
                composition = composition
            )
            isClicked = false
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
                isClicked = true
                onClick()
            }
    )
}