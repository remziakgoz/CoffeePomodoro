package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@Composable
fun CoffeeAnimation(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    animationProgress: Float,
    contentScale: ContentScale = ContentScale.Fit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("coffeeCup.json"))
    
    val coffeeColor = Color(0xFFE8B85C)
    

    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam left", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam middle", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam right", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam left 4", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam middle 4", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam right 4", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam left 5", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam middle 5", "**")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = ColorFilter.tint(coffeeColor),
            keyPath = arrayOf("steam right 5", "**")
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LottieAnimation(
            composition = composition,
            progress = { animationProgress.coerceIn(0f, 1f) },
            contentScale = contentScale,
            clipToCompositionBounds = false,
            dynamicProperties = dynamicProperties,
            modifier = modifier
        )
    }
}