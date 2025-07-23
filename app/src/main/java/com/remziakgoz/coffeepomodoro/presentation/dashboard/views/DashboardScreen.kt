package com.remziakgoz.coffeepomodoro.presentation.dashboard.views

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.components.CoffeeProgressCard
import com.remziakgoz.coffeepomodoro.presentation.components.CompactWeeklyProgress
import com.remziakgoz.coffeepomodoro.presentation.components.AchievementSection
import com.remziakgoz.coffeepomodoro.presentation.components.CoffeeTipSection
import com.remziakgoz.coffeepomodoro.presentation.components.QuickStatsSection
import com.remziakgoz.coffeepomodoro.presentation.dashboard.DashboardViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val successText = when {
        uiState.isGoalReached -> "Goal Achieved!\nAwesome Work!"
        uiState.dashboardData.todayCups > 0 -> "Keep Going!\nCups Drank"
        else -> "Start Your Day!\nFirst Cup"
    }

    Box(
        modifier = modifier
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
            // Error handling
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painterResource(id = R.drawable.cup11fordb),
                        contentDescription = "Success Cup",
                        modifier = modifier.size(90.dp)
                    )
                    Text(
                        text = successText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 26.sp
                    )

                }
                
                Row(
                    modifier = modifier.align(Alignment.CenterEnd)
                ) {
                    // Add Test Session Button (development only)
                    IconButton(
                        onClick = { viewModel.addTestSession() },
                        modifier = modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Test Session",
                            tint = Color.White.copy(alpha = 0.7f),
                        )
                    }
                    
                    // Refresh Button with loading state
                    IconButton(
                        onClick = { viewModel.refreshData() },
                        modifier = modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isLoading || uiState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = modifier.size(24.dp),
                                color = Color.White.copy(alpha = 0.8f),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Data",
                                tint = Color.White.copy(alpha = 0.8f),
                            )
                        }
                    }
                }

            }

            Text(
                text = "Cups Drank",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = modifier.padding(start = 20.dp, top = 30.dp)
            )

            Spacer(modifier = modifier.size(22.dp))

            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = modifier.padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CoffeeProgressCard(
                        counter = uiState.dashboardData.todayCups,
                        imageId = R.drawable.cup1fordb,
                        dayProgress = "Today"
                    )
                    Spacer(modifier =  modifier.size(8.dp))
                    CoffeeProgressCard(
                        counter = uiState.dashboardData.weeklyCups,
                        imageId = R.drawable.cup7fordb,
                        dayProgress = "Week"
                    )
                    Spacer(modifier =  modifier.size(8.dp))
                    CoffeeProgressCard(
                        counter = uiState.dashboardData.monthlyCups,
                        imageId = R.drawable.cupcorefordb2,
                        dayProgress = "Month"
                    )
                }
            }
            Spacer(modifier = modifier.size(24.dp))

            // Modern Weekly Progress Report
            CompactWeeklyProgress(
                progress = uiState.dashboardData.weeklyCups,
                goal = uiState.dashboardData.weeklyGoal,
                dailyData = uiState.dailyProgressData,
                modifier = modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = modifier.size(12.dp))

            // Bottom Section - Responsive Layout
            
            // Option 1: Achievement Badges
            AchievementSection(
                goalReached = uiState.isGoalReached,
                threeDayStreak = uiState.hasThreeDayStreak,
                morningStarUnlocked = uiState.isMorningStar,
                consistencyUnlocked = uiState.isConsistent,
                modifier = modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = modifier.size(8.dp))
            
            // Option 2: Quick Stats
            QuickStatsSection(
                dailyAverage = uiState.dashboardData.dailyAverage,
                bestStreak = uiState.dashboardData.bestStreak,
                totalCups = uiState.dashboardData.totalSessions,
                modifier = modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = modifier.size(8.dp))
            
            // Option 3: Coffee Tip
            CoffeeTipSection(
                modifier = modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = modifier.size(24.dp))

        }

    }


}

@Preview(showBackground = true)
@Composable
fun Preview(modifier: Modifier = Modifier) {
    DashboardScreen()
}