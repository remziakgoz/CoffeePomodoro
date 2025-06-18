package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Composable
fun SegmentDigit(
    digit: Int,
    segmentWidth: Float,
    segmentLength: Float,
    modifier: Modifier = Modifier
) {
    val segments = mapOf(
        0 to listOf(true, true, true, true, true, true, false),
        1 to listOf(false, true, true, false, false, false, false),
        2 to listOf(true, true, false, true, true, false, true),
        3 to listOf(true, true, true, true, false, false, true),
        4 to listOf(false, true, true, false, false, true, true),
        5 to listOf(true, false, true, true, false, true, true),
        6 to listOf(true, false, true, true, true, true, true),
        7 to listOf(true, true, true, false, false, false, false),
        8 to listOf(true, true, true, true, true, true, true),
        9 to listOf(true, true, true, true, false, true, true)
    )

    val active = segments[digit] ?: List(7) { false }

    Canvas(modifier = modifier) {
        val w = segmentWidth
        val l = segmentLength

        val centerX = size.width / 2

        // A
        if (active[0]) drawRect(Color.Black, topLeft = Offset(centerX - l/2, 0f), size = Size(l, w))
        // B
        if (active[1]) drawRect(Color.Black, topLeft = Offset(centerX + l/2 - w, w), size = Size(w, l))
        // C
        if (active[2]) drawRect(Color.Black, topLeft = Offset(centerX + l/2 - w, l + 2*w), size = Size(w, l))
        // D
        if (active[3]) drawRect(Color.Black, topLeft = Offset(centerX - l/2, 2*l + 2*w), size = Size(l, w))
        // E
        if (active[4]) drawRect(Color.Black, topLeft = Offset(centerX - l/2, l + 2*w), size = Size(w, l))
        // F
        if (active[5]) drawRect(Color.Black, topLeft = Offset(centerX - l/2, w), size = Size(w, l))
        // G
        if (active[6]) drawRect(Color.Black, topLeft = Offset(centerX - l/2, l + w), size = Size(l, w))
    }
}

@Composable
fun DigitalClockCanvas(minutes: Int, seconds: Int) {
    val minDigits = minutes.toString().padStart(2, '0').map { it.digitToInt() }
    val secDigits = seconds.toString().padStart(2, '0').map { it.digitToInt() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        minDigits.forEach {
            SegmentDigit(digit = it, segmentWidth = 8f, segmentLength = 50f, modifier = Modifier.size(40.dp, 80.dp))
            Spacer(modifier = Modifier.run { width(8.dp) })
        }

        Text(":", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterVertically).padding(bottom = 75.dp))

        secDigits.forEach {
            Spacer(modifier = Modifier.width(8.dp))
            SegmentDigit(digit = it, segmentWidth = 8f, segmentLength = 50f, modifier = Modifier.size(40.dp, 80.dp))
        }
    }
}


@Composable
fun PomodoroWithCanvasClock(shouldStart: Boolean) {
    var timeLeft by remember { mutableIntStateOf(25 * 60) }

    LaunchedEffect(shouldStart) {
        if (shouldStart) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    DigitalClockCanvas(minutes = minutes, seconds = seconds)
}