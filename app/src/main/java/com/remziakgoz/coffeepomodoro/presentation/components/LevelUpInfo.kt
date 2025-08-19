// LevelsDialogV3.kt
package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.dashboard.DashboardUiState
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CheckTint
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CtaEnd
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CtaStart
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CurrentBottom
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CurrentStripe
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CurrentTop
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.GlassStroke
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L1A
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L1B
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L2A
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L2B
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L3A
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L3B
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L4A
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L4B
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L5A
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.L5B
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.LockTint
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.PanelBottom
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.PanelTop
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.TileBottom
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.TileTop
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.TitleBlue

private val PanelBorderBrush = Brush.horizontalGradient(
    listOf(Color(0xFF6A5AF9), Color(0xFF3EC5FF)) // mor → mavi
)
/* -------- Timeline -------- */
private val IconCapsuleSize = 64.dp
private val StartPaddingBeforeTrack = 14.dp
private val TrackXOffset = StartPaddingBeforeTrack + (IconCapsuleSize / 2)
private val TrackWidth = 2.dp
private val BetweenItemSpace = 12.dp

/* -------- Model -------- */
data class LevelSpec(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val index: Int,
    val unlocked: Boolean,
    val current: Boolean
)

/* ---------- Public API ---------- */
@Composable
fun LevelsDialogV3(
    show: Boolean,
    onDismiss: () -> Unit,
    ui: DashboardUiState
) {
    if (!show) return
    val specs = buildLevelSpecs(ui)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 0.dp,
            shadowElevation = 12.dp,
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.verticalGradient(listOf(PanelTop, PanelBottom)))
                    .border(BorderStroke(2.dp, PanelBorderBrush), RoundedCornerShape(28.dp))
                    .padding(1.dp)
                    .border(BorderStroke(1.dp, GlassStroke), RoundedCornerShape(27.dp))
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LEVELS",
                    color = TitleBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        itemsIndexed(specs) { idx, spec ->
                            val isLast = idx == specs.lastIndex

                            LevelRowCardV3(spec)

                            if (!isLast) {
                                CenterConnector(
                                    brush = levelBrush(spec.index, spec.unlocked, spec.current),
                                    active = spec.unlocked || spec.current
                                )
                            }
                        }
                    }
                }

                PrimaryGradientButton(
                    text = "OK",
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(top = 14.dp, bottom = 6.dp)
                        .fillMaxWidth()
                        .height(54.dp)
                )
            }
        }
    }
}

/* -------- Level Row – track -------- */
@Composable
private fun LevelRowCardV3(spec: LevelSpec) {
    val bg = if (spec.current)
        Brush.verticalGradient(listOf(CurrentTop, CurrentBottom))
    else
        Brush.verticalGradient(listOf(TileTop, TileBottom))

    val rowAlpha = if (spec.unlocked) 1f else 0.7f
    val borderBrush = levelBrush(spec.index, spec.unlocked, spec.current)
    val trackBrush = if (spec.unlocked || spec.current)
        borderBrush
    else
        Brush.verticalGradient(listOf(Color(0xFFE3E6F0), Color(0xFFDADFEA)))

    Box(Modifier.fillMaxWidth().alpha(rowAlpha)) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.Transparent,
            border = BorderStroke(1.5.dp, borderBrush),
            shadowElevation = if (spec.current) 8.dp else 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(bg)
                    .clip(RoundedCornerShape(22.dp))
                    .heightIn(min = 84.dp)
            ) {
                if (spec.current) Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(CurrentStripe)
                )

                Spacer(Modifier.width(StartPaddingBeforeTrack))

                Box(
                    modifier = Modifier
                        .size(IconCapsuleSize)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = spec.iconRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(spec.title, color = TitleBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(spec.subtitle, color = TitleBlue.copy(alpha = .60f), fontSize = 14.sp)
                }

                val tint = if (spec.unlocked) CheckTint else LockTint
                val icon = if (spec.unlocked) Icons.Filled.CheckCircle else Icons.Filled.Lock
                Box(
                    modifier = Modifier
                        .padding(end = 14.dp)
                        .size(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = tint, modifier = Modifier.size(if (spec.unlocked) 22.dp else 18.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(TrackWidth)
                .offset(x = TrackXOffset)
                .background(trackBrush)
                .align(Alignment.CenterStart)
        )
    }
}

@Composable
private fun CenterConnector(
    brush: Brush,
    active: Boolean
) {
    val dim = if (active) brush else Brush.verticalGradient(
        listOf(Color(0xFFE3E6F0), Color(0xFFDADFEA))
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BetweenItemSpace)
    ) {
        Box(
            modifier = Modifier
                .offset(x = TrackXOffset)
                .width(TrackWidth)
                .fillMaxHeight()
                .background(dim)
                .align(Alignment.CenterStart)
        )
    }
}

/* -------- Gradient CTA -------- */
@Composable
private fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(CtaStart, CtaEnd)))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Text(text, color = Color.White, fontWeight = FontWeight.SemiBold) }
}

private fun levelBrush(index: Int, unlocked: Boolean, current: Boolean): Brush {
    val (c1, c2) = when (index) {
        1 -> L1A to L1B
        2 -> L2A to L2B
        3 -> L3A to L3B
        4 -> L4A to L4B
        else -> L5A to L5B
    }
    val alpha = when {
        current  -> 0.95f
        unlocked -> 0.80f
        else     -> 0.40f
    }
    return Brush.linearGradient(listOf(c1.copy(alpha = alpha), c2.copy(alpha = alpha)))
}

private fun buildLevelSpecs(ui: DashboardUiState): List<LevelSpec> {
    val total = ui.stats.totalCups
    val streak = ui.stats.currentStreak
    val weeklyDone = ui.stats.weeklyCups >= ui.stats.weeklyGoal
    val current = ui.level

    fun unlockedFor(i: Int) = when (i) {
        1 -> total >= 10
        2 -> total >= 50 && weeklyDone
        3 -> total >= 250 && streak >= 21
        4 -> total >= 500 && streak >= 30
        else -> total >= 1000
    }

    return listOf(
        LevelSpec("Espresso Master",     "10 total cups",                 R.drawable.cuplevel1, 1, unlockedFor(1), current == 1),
        LevelSpec("Cappuccino Champion", "50 total + weekly goal",        R.drawable.cuplevel2, 2, unlockedFor(2), current == 2),
        LevelSpec("Latte Legend",        "250 total + 21-day streak",     R.drawable.cuplevel3, 3, unlockedFor(3), current == 3),
        LevelSpec("Mocha Monarch",       "500 total + 30-day streak",     R.drawable.cuplevel4, 4, unlockedFor(4), current == 4),
        LevelSpec("Coffee Conqueror",    "1000 total cups",               R.drawable.cuplevel5, 5, unlockedFor(5), current == 5),
    )
}
