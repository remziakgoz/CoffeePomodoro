package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Compact version optimized for dark background
@Composable
fun CompactWeeklyProgress(
    progress: Int = 0,
    goal: Int = 35,
    dailyData: List<Int> = emptyList(),
    modifier: Modifier = Modifier
) {
    val safeGoal = goal.coerceAtLeast(1)
    val ratio = (progress.toFloat() / safeGoal).coerceIn(0f, 1f)
    val safeDaily = dailyData.takeIf { it.size == 7 } ?: List(7) { 0 }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Black.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Compact Progress Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactCircularProgress(
                progress = progress,
                goal = safeGoal,
                ratioOverride = ratio
            )
            
            WeeklyStatsColumn(
                progress = progress,
                goal = safeGoal
            )
        }
        
        // Compact Daily Dots
        CompactDailyDots(dailyData = safeDaily)
    }
}

@Composable
private fun CompactCircularProgress(
    progress: Int,
    goal: Int,
    modifier: Modifier = Modifier,
    ratioOverride: Float? = null
) {
    val target = ratioOverride ?: (progress.toFloat() / goal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "compact_progress"
    )
    
    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(70.dp)) {
            val strokeWidth = 6.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            // Background circle
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            
            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFFFD700),
                        Color(0xFFFFA500),
                        Color(0xFFFF8C00)
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                ),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(1f, 1f),
                        blurRadius = 3f
                    )
                )
            )
            Text(
                text = "/$goal",
                fontSize = 10.sp,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(1f, 1f),
                        blurRadius = 2f
                    )
                )
            )
        }
    }
}

@Composable
private fun WeeklyStatsColumn(
    progress: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val percentage = ((progress * 100f) / goal).toInt().coerceIn(0, 100)
    val remaining = (goal - progress).coerceAtLeast(0)
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Weekly Progress",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
        Text(
            text = "$percentage% Complete",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
        Text(
            text = "$remaining cups remaining",
            fontSize = 12.sp,
            color = Color.White,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    offset = Offset(1f, 1f),
                    blurRadius = 3f
                )
            )
        )
    }
}

@Composable
private fun CompactDailyDots(
    dailyData: List<Int>,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val safeDaily = if (dailyData.size == 7) dailyData else List(7) { 0 }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "This Week",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            safeDaily.forEachIndexed { index, cups ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    cups >= 5 -> Color(0xFF4CAF50) // Green
                                    cups >= 3 -> Color(0xFFFF9800) // Orange  
                                    cups > 0 -> Color(0xFFFFA726) // Light orange
                                    else -> Color.White.copy(alpha = 0.6f) // More visible
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cups > 0) {
                            Text(
                                text = cups.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = days.getOrElse(index) { "N/A" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.9f),
                                offset = Offset(1f, 1f),
                                blurRadius = 3f
                            )
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview2(modifier: Modifier = Modifier) {
    CompactWeeklyProgress()
}
