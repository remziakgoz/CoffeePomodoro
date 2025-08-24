package com.remziakgoz.coffeepomodoro.presentation.dashboard.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.components.AchievementSection
import com.remziakgoz.coffeepomodoro.presentation.components.CoffeeProgressCard
import com.remziakgoz.coffeepomodoro.presentation.components.CoffeeTipSection
import com.remziakgoz.coffeepomodoro.presentation.components.CompactWeeklyProgress
import com.remziakgoz.coffeepomodoro.presentation.components.LevelUpFlowOverlay
import com.remziakgoz.coffeepomodoro.presentation.components.QuickStatsSection
import com.remziakgoz.coffeepomodoro.presentation.components.LevelsDialogV3
import com.remziakgoz.coffeepomodoro.presentation.dashboard.DashboardViewModel
import com.remziakgoz.coffeepomodoro.presentation.root.isLandscape

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onSwipeToPomodoroScreen: () -> Unit,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    isDashboardVisible: Boolean = true
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    var showLevelDialog by remember { mutableStateOf(false) }
    val isLandscapeMode = isLandscape()

    // Trigger level up animation when Dashboard becomes visible
    LaunchedEffect(isDashboardVisible) {
        if (isDashboardVisible) {
            dashboardViewModel.onDashboardBecameVisible()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .paint(
                painterResource(R.drawable.dashbg),
                contentScale = ContentScale.Crop
            )
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // Current Level Cup section
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        if (isLandscapeMode) 4.dp else 4.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = modifier.fillMaxWidth()
                ) {
                    Image(
                        painterResource(id = uiState.levelIconRes),
                        contentDescription = "Current Level Cup",
                        modifier = modifier
                            .size(90.dp)
                            .pointerInput(Unit) {
                                detectTapGestures { showLevelDialog = true }
                            }
                    )
                    Text(
                        text = "${uiState.levelTitle}\nCups Drank",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 26.sp
                    )
                }
            }

            Text(
                text = "Cups Drank",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (isLandscapeMode) 0.dp else 20.dp,
                        top = if (isLandscapeMode) 0.dp else 30.dp
                    ),
                textAlign = if (isLandscapeMode) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start
            )

            Spacer(modifier = modifier.size(if (isLandscapeMode) 12.dp else 22.dp))

            // Coffee Progress Cards section with landscape adaptation
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLandscapeMode) {
                    // Landscape: Larger cards spread across screen
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CoffeeProgressCard(
                            counter = uiState.stats.todayCups,
                            imageId = R.drawable.cup1fordb,
                            dayProgress = "Today"
                        )
                        CoffeeProgressCard(
                            counter = uiState.stats.weeklyCups,
                            imageId = R.drawable.cup7fordb,
                            dayProgress = "Week"
                        )
                        CoffeeProgressCard(
                            counter = uiState.stats.monthlyCups,
                            imageId = R.drawable.cupcorefordb2,
                            dayProgress = "Month"
                        )
                    }
                } else {
                    // Portrait: Original design
                    Row(
                        modifier = modifier.padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CoffeeProgressCard(
                            counter = uiState.stats.todayCups,
                            imageId = R.drawable.cup1fordb,
                            dayProgress = "Today"
                        )
                        Spacer(modifier = modifier.size(8.dp))
                        CoffeeProgressCard(
                            counter = uiState.stats.weeklyCups,
                            imageId = R.drawable.cup7fordb,
                            dayProgress = "Week"
                        )
                        Spacer(modifier = modifier.size(8.dp))
                        CoffeeProgressCard(
                            counter = uiState.stats.monthlyCups,
                            imageId = R.drawable.cupcorefordb2,
                            dayProgress = "Month"
                        )
                    }
                }
            }

            if (showLevelDialog) {
                LevelsDialogV3(true, { showLevelDialog = false }, uiState)
            }
            Spacer(modifier = modifier.size(24.dp))

            // Modern Weekly Progress Report
            CompactWeeklyProgress(
                progress = uiState.stats.weeklyCups,
                goal = uiState.stats.weeklyGoal,
                dailyData = uiState.stats.dailyData,
                modifier = modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = modifier.size(12.dp))

            // Achievement Badges
            AchievementSection(
                modifier = modifier.padding(horizontal = 16.dp),
                progress = 25,
                achievements = uiState.achievements
            )

            Spacer(modifier = modifier.size(8.dp))

            // Quick Stats
            QuickStatsSection(
                modifier = modifier.padding(horizontal = 16.dp),
                dailyAvg = uiState.quickDailyAvg,
                bestStreak = uiState.stats.bestStreak,
                total = uiState.stats.totalCups
            )

            Spacer(modifier = modifier.size(8.dp))

            // Coffee Tip
            CoffeeTipSection(
                modifier = modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = modifier.size(24.dp))
        }

        if (uiState.shouldShowLevelUpAnimation) {
            LevelUpFlowOverlay(
                ui = uiState,
                visible = true,
                lottieResId = "celebration.json",
                onContinue = { dashboardViewModel.consumeLevelUpAnimation() }
            )
        }
    }
}
