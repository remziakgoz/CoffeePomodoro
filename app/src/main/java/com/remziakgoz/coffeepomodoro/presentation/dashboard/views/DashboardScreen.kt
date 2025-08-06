package com.remziakgoz.coffeepomodoro.presentation.dashboard.views

import android.text.Layout
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.remziakgoz.coffeepomodoro.presentation.pomodoro.PomodoroViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onSwipeToPomodoroScreen: () -> Unit,
    viewModel: PomodoroViewModel = hiltViewModel()
) {

    val successText by remember { mutableStateOf("Espresso Master\nCups Drank") }
    val uiState by viewModel.uiState.collectAsState()
    val cycleCount = uiState.cycleCount


    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    if (dragAmount > 80) {
                        onSwipeToPomodoroScreen()
                    }
                }
            }
    ) {

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
                            painterResource(id = R.drawable.successcup),
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
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = modifier
                            .size(64.dp)
                            .align(Alignment.CenterEnd),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart Dashboard Button",
                            modifier = modifier
                                .fillMaxSize(),
                            tint = Color.White.copy(alpha = 0.8f),
                        )
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
                            counter = cycleCount,
                            imageId = R.drawable.cup1fordb,
                            dayProgress = "Today"
                        )
                        Spacer(modifier = modifier.size(8.dp))
                        CoffeeProgressCard(
                            counter = cycleCount,
                            imageId = R.drawable.cup7fordb,
                            dayProgress = "Week"
                        )
                        Spacer(modifier = modifier.size(8.dp))
                        CoffeeProgressCard(
                            counter = cycleCount,
                            imageId = R.drawable.cupcorefordb2,
                            dayProgress = "Month"
                        )
                    }
                }
                Spacer(modifier = modifier.size(24.dp))

                // Modern Weekly Progress Report
                CompactWeeklyProgress(
                    progress = cycleCount,
                    goal = 35,
                    dailyData = listOf(3, 2, 4, 1, 5, 0, 0),
                    modifier = modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = modifier.size(12.dp))

                // Bottom Section - Responsive Layout

                // Option 1: Achievement Badges
                AchievementSection(
                    modifier = modifier.padding(horizontal = 16.dp),
                    progress = cycleCount
                )

                Spacer(modifier = modifier.size(8.dp))

                // Option 2: Quick Stats
                QuickStatsSection(
                    modifier = modifier.padding(horizontal = 16.dp),
                    progress = cycleCount
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


}

@Preview(showBackground = true)
@Composable
fun Preview(modifier: Modifier = Modifier) {
    DashboardScreen(
        modifier = modifier,
        onSwipeToPomodoroScreen = {}
    )
}