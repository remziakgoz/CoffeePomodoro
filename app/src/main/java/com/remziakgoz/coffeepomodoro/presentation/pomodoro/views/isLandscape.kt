package com.remziakgoz.coffeepomodoro.presentation.pomodoro.views

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
fun isLandscape(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val size = LocalWindowInfo.current.containerSize
        size.width > size.height
    } else {
        val config = LocalConfiguration.current
        config.screenWidthDp > config.screenHeightDp
    }
}
