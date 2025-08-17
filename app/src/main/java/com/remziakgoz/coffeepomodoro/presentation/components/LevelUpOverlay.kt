package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.dashboard.DashboardUiState
import kotlinx.coroutines.delay

private enum class LevelUpPhase { Anim, Card }

private val Amber = Color(0xFFFFC043)
private val Espresso = Color(0xFF2F261E)
private val AccentPurple = Color(0xFFC58CFF) // TOTAL
private val AccentGreen = Color(0xFF39D353) // PERFECT/Streak
private val AccentBlue = Color(0xFF49B3FF) // BLAZING/Weekly


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LevelUpFlowOverlay(
    ui: DashboardUiState,
    visible: Boolean,
    onContinue: () -> Unit,
    lottieResId: String,
    animSpeed: Float = 1.0f,
    allowSkipTap: Boolean = true,
    minAnimVisibleMs: Long = 1400
) {
    if (!visible) return

    var phase by remember(visible) { mutableStateOf(LevelUpPhase.Anim) }
    val animStartMs = remember(visible) { System.currentTimeMillis() } // ✅ başlangıç zamanı

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(100f)
            .background(Color.Black.copy(alpha = 0.28f))
            .pointerInput(visible, phase) {
                if (allowSkipTap) {
                    awaitPointerEventScope {
                        while (visible && phase == LevelUpPhase.Anim) {
                            awaitPointerEvent()
                            phase = LevelUpPhase.Card
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = phase,
            transitionSpec = { fadeIn() with fadeOut() },
            label = "level-up-phase"
        ) { p ->
            when (p) {
                LevelUpPhase.Anim -> {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.Asset(lottieResId)
                    )
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = 1,
                        speed = animSpeed
                    )
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(240.dp)
                    )

                    LaunchedEffect(progress) {
                        if (progress >= 1f) {
                            val elapsed = System.currentTimeMillis() - animStartMs
                            val wait = (minAnimVisibleMs - elapsed).coerceAtLeast(0L)
                            if (wait > 0) delay(wait)
                            delay(250)
                            phase = LevelUpPhase.Card
                        }
                    }
                }

                LevelUpPhase.Card -> {
                    CelebrationCardContent(ui = ui, onContinue = onContinue)
                }
            }
        }
    }
}


@Composable
private fun CelebrationCardContent(
    ui: DashboardUiState,
    onContinue: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(Unit) { haptics.performHapticFeedback(HapticFeedbackType.LongPress) }

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "scale"
    )
    val alpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(200), label = "alpha")

    OutlinedCard(
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = .7f),
                    Color.White.copy(alpha = .2f)
                )
            )
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .graphicsLayer { this.scaleX = scale; this.scaleY = scale; this.alpha = alpha }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Level up!",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Amber
            )
            Spacer(Modifier.height(10.dp))

            // Cup + radial glow
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(130.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Amber.copy(alpha = .45f), Color.Transparent)
                        ),
                        radius = size.minDimension / 2.0f
                    )
                }
                Image(
                    painter = painterResource(id = ui.levelIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(130.dp)
                )
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = ui.levelTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )

            Spacer(Modifier.height(14.dp))

            // 3 pill – modern chip style
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatTile(
                    label = "TOTAL CUPS",
                    value = ui.stats.totalCups.toString(),
                    accent = AccentPurple,
                    valueColor = AccentPurple,
                    modifier = Modifier.weight(1f),
                    leading = {
                        Icon(
                            painter = painterResource(id = R.drawable.cup2forlevelup),
                            contentDescription = "cup icon",
                            tint = Color.Unspecified, // or Color.White, AccentGreen vs.
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                StatTile(
                    label = "STREAK",
                    value = "${ui.stats.currentStreak}d",
                    accent = AccentGreen,
                    valueColor = AccentGreen,
                    modifier = Modifier.weight(1f),
                    leading = {
                        Icon(
                            painter = painterResource(id = R.drawable.fire),
                            contentDescription = "fire icon",
                            tint = Color.Unspecified, // or Color.White, AccentGreen vs.
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )

                val weeklyPct = ((ui.stats.weeklyCups.toFloat() / ui.stats.weeklyGoal)
                    .coerceIn(0f, 1f) * 100).toInt()

                StatTile(
                    label = "WEEKLY",
                    value = "$weeklyPct%",
                    accent = AccentBlue,
                    valueColor = AccentBlue,
                    modifier = Modifier.weight(1f),
                    leading = {
                        Icon(
                            painter = painterResource(id = R.drawable.target),
                            contentDescription = "target icon",
                            tint = Color.Unspecified, // or Color.White, AccentGreen vs.
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }

            Spacer(Modifier.height(18.dp))

            // Gradient CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF6372FF), Color(0xFF3F55D2))
                        )
                    )
                    .clickable { onContinue() },
                contentAlignment = Alignment.Center
            ) {
                Text("CONTINUE", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
fun StatTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit,
    valueColor: Color = Color(0xFF2A2A2A),
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(2.dp, accent.copy(alpha = 0.55f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier
    ) {
        Column {
            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accent.copy(alpha = 0.90f),
                                accent.copy(alpha = 0.75f)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }

            // BODY
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp),
                        contentAlignment = Alignment.Center //
                    ) {
                        leading()
                    }
                    Text(
                        text = value,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = valueColor,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    StatTile("Text", "100%", Color.Blue, modifier = Modifier, {
        Icon(
        painter = painterResource(id = R.drawable.target),
        contentDescription = "target icon",
        tint = Color.Unspecified, // or Color.White, AccentGreen vs.
        modifier = Modifier.size(24.dp)
    )
    }, valueColor = Color.Black)
}