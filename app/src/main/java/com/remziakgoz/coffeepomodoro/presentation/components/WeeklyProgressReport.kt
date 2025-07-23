@file:OptIn(ExperimentalMaterial3Api::class)

package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modern Weekly Progress Report Component
@Composable
fun WeeklyProgressReport(
    weeklyGoal: Int = 35, // 5 cups per day * 7 days
    currentProgress: Int = 15,
    dailyData: List<Int> = listOf(3, 2, 4, 1, 5, 0, 0), // Son 7 gün
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Option 1: Circular Progress Ring
        CircularProgressReport(
            progress = currentProgress,
            goal = weeklyGoal
        )
        
        // Option 2: Modern Linear Progress Bar
        LinearProgressReport(
            progress = currentProgress,
            goal = weeklyGoal
        )
        
        // Option 3: Daily Progress Dots
        DailyProgressDots(dailyData = dailyData)
        
        // Option 4: Achievement Streaks
        StreakCounter(currentStreak = 3)
    }
}

// 1. Circular Progress Ring (En Modern)
@Composable
fun CircularProgressReport(
    progress: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.toFloat() / goal,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "progress"
    )
    
    Box(
        modifier = modifier
            .size(120.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF8B4513).copy(alpha = 0.1f),
                        Color(0xFF5D2A0A).copy(alpha = 0.2f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            // Background circle
            drawCircle(
                color = Color(0xFF8B4513).copy(alpha = 0.2f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            
            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFD2691E),
                        Color(0xFF8B4513),
                        Color(0xFFCD853F)
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
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B4513)
            )
            Text(
                text = "/ $goal",
                fontSize = 12.sp,
                color = Color(0xFF8B4513).copy(alpha = 0.7f)
            )
        }
    }
}

// 2. Modern Linear Progress Bar
@Composable
fun LinearProgressReport(
    progress: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.toFloat() / goal,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label = "linear_progress"
    )
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Goal",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8B4513)
            )
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B4513)
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF8B4513).copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFD2691E),
                                Color(0xFF8B4513)
                            )
                        )
                    )
            )
        }
    }
}

// 3. Daily Progress Dots
@Composable
fun DailyProgressDots(
    dailyData: List<Int>,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Daily Progress",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8B4513)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dailyData.forEachIndexed { index, cups ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    cups >= 5 -> Color(0xFF228B22) // Green for excellent
                                    cups >= 3 -> Color(0xFFD2691E) // Orange for good  
                                    cups > 0 -> Color(0xFFDEB887) // Light brown for some
                                    else -> Color(0xFF8B4513).copy(alpha = 0.3f) // Gray for none
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cups > 0) cups.toString() else "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Text(
                        text = days[index],
                        fontSize = 10.sp,
                        color = Color(0xFF8B4513).copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// 4. Streak Counter
@Composable
fun StreakCounter(
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    val animatedStreak by animateIntAsState(
        targetValue = currentStreak,
        animationSpec = tween(durationMillis = 800),
        label = "streak"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8B4513).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Current Streak",
                    fontSize = 14.sp,
                    color = Color(0xFF8B4513).copy(alpha = 0.8f)
                )
                Text(
                    text = "$animatedStreak days",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B4513)
                )
            }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA500)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔥",
                    fontSize = 20.sp
                )
            }
        }
    }
}

// Glass Morphism Effect (Bonus)
@Composable
fun GlassMorphismCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .blur(1.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        content()
    }
} 