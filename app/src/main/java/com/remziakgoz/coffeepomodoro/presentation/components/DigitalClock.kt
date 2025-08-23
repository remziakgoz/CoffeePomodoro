package com.remziakgoz.coffeepomodoro.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.remziakgoz.coffeepomodoro.R

private val londrinaOutlineRegular = FontFamily(
    Font(R.font.londrinaoutlineregular)
)

@SuppressLint("DefaultLocale")
@Composable
fun PomodoroWithCanvasClock(
    remainingTime: Long,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 110.sp
) {
    val minutes = (remainingTime / 1000 / 60).toInt()
    val seconds = (remainingTime / 1000 % 60).toInt()
    
    val timeString = String.format("%02d:%02d", minutes, seconds)
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeString,
            fontFamily = londrinaOutlineRegular,
            fontSize = fontSize,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}