package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GooeySlimeLoader(
    modifier: Modifier = Modifier,
    diameter: Dp = 56.dp,
    baseColor: Color = Color(0xFF34E39A),   // 0xFF34E39A
    accentColor: Color = Color(0xFF22C07A), // 0xFF22C07A
    trackAlpha: Float = 0.10f
) {
    val infinite = rememberInfiniteTransition(label = "gooey")

    val angle by infinite.animateFloat(
        0f, 360f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "angle"
    )
    
    val sweep by infinite.animateFloat(
        initialValue = 240f,
        targetValue = 320f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 1200
                240f at 0
                320f at 600
                240f at 1200
            }
        ),
        label = "sweep"
    )

    val thicknessMul by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 900
                0.85f at 0
                1.15f at 450
                0.85f at 900
            },
            RepeatMode.Reverse
        ),
        label = "thickness"
    )

    val dropletSquash by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 700
                0.9f at 0
                1.15f at 350
                0.9f at 700
            },
            RepeatMode.Reverse
        ),
        label = "droplet"
    )

    Canvas(
        modifier
            .size(diameter)
            .graphicsLayer {
                val s = 0.99f + (thicknessMul - 1f) * 0.06f
                scaleX = s
                scaleY = s
            }
    ) {
        val r = size.minDimension / 2f
        val ringRadius = r * 0.70f
        val baseStroke = r * 0.22f
        val stroke = baseStroke * thicknessMul

        drawArc(
            color = Color.Black.copy(trackAlpha),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
            size = Size(ringRadius * 2, ringRadius * 2),
            style = Stroke(width = baseStroke * 0.8f, cap = StrokeCap.Round)
        )

        drawArc(
            color = baseColor.copy(alpha = 0.25f),
            startAngle = angle,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
            size = Size(ringRadius * 2, ringRadius * 2),
            style = Stroke(width = stroke * 1.6f, cap = StrokeCap.Round)
        )

        val ringBrush = Brush.sweepGradient(
            0f to baseColor,
            0.5f to accentColor,
            1f to baseColor
        )
        drawArc(
            brush = ringBrush,
            startAngle = angle,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
            size = Size(ringRadius * 2, ringRadius * 2),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        drawArc(
            brush = ringBrush,
            startAngle = angle + (sweep - 40f),
            sweepAngle = 40f,
            useCenter = false,
            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
            size = Size(ringRadius * 2, ringRadius * 2),
            style = Stroke(width = stroke * 1.35f, cap = StrokeCap.Round)
        )

        val endAngle = angle + sweep
        val endRad = Math.toRadians(endAngle.toDouble()).toFloat()
        val end = Offset(
            x = center.x + ringRadius * cos(endRad),
            y = center.y + ringRadius * sin(endRad)
        )

        val tangentDeg = endAngle + 90f

        val dropR = (stroke * 0.85f) * dropletSquash
        withTransform({
            rotate(degrees = tangentDeg, pivot = end)
        }) {
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(baseColor, accentColor.copy(alpha = 0.8f))
                ),
                topLeft = Offset(end.x - dropR * 1.1f, end.y - dropR * 0.9f),
                size = Size(dropR * 2.2f, dropR * 1.8f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.28f),
                radius = dropR * 0.35f,
                center = end + Offset(-dropR * 0.25f, -dropR * 0.25f)
            )
        }

        fun trail(atDeg: Float, scale: Float, alpha: Float) {
            val rad = Math.toRadians(atDeg.toDouble()).toFloat()
            val pos = Offset(
                x = center.x + ringRadius * cos(rad),
                y = center.y + ringRadius * sin(rad)
            )
            drawCircle(
                color = baseColor.copy(alpha = alpha),
                center = pos,
                radius = dropR * scale
            )
        }
        trail(endAngle - 22f, 0.45f, 0.35f)
        trail(endAngle - 44f, 0.30f, 0.25f)
    }
}

