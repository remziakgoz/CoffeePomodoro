package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle

// 1. Achievement Badges Section (Responsive)
@Composable
fun AchievementSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.6f),
                        Color.Black.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Today's Achievements",
            fontSize = 14.sp,
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
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AchievementBadge(
                icon = "🏆",
                title = "Goal Reached",
                isUnlocked = true
            )
            AchievementBadge(
                icon = "🔥",
                title = "3 Day Streak",
                isUnlocked = true
            )
            AchievementBadge(
                icon = "⭐",
                title = "Morning Star",
                isUnlocked = false
            )
            AchievementBadge(
                icon = "💪",
                title = "Consistency",
                isUnlocked = false
            )
        }
    }
}

@Composable
private fun AchievementBadge(
    icon: String,
    title: String,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA500)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.3f),
                                Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
        }
        
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
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

// 2. Coffee Tip of the Day
@Composable
fun CoffeeTipSection(
    modifier: Modifier = Modifier
) {
    val tips = listOf(
        // ☕ Coffee Brewing Tips
        "☕ The ideal water temperature for brewing coffee is 195-205°F",
        "🌱 Fresh coffee beans should be used within 2-4 weeks of roasting",
        "⏰ The perfect extraction time for espresso is 25-30 seconds",
        "💧 Use filtered water for the best coffee taste",
        "🔄 Grind your coffee beans just before brewing for maximum flavor",
        "⚖️ The golden ratio for coffee is 1:15 to 1:17 (coffee to water)",
        "🫘 Store coffee beans in an airtight container away from light",
        "🥶 Never refrigerate your coffee beans - it creates moisture",
        "☕ Pour-over coffee takes 4-6 minutes for optimal extraction",
        "🌡️ Let your coffee cool for 2-3 minutes before drinking for best taste",
        
        // 🧠 Productivity & Focus Tips
        "🧠 Drink coffee 30 minutes before starting work for peak focus",
        "⏰ Try the Pomodoro Technique: 25min work + 5min coffee break",
        "🚫 Avoid coffee after 2 PM to maintain good sleep quality",
        "💪 Coffee improves physical performance by 11-12% on average",
        "🎯 Caffeine peaks in your system 30-60 minutes after consumption",
        "🧘 Take mindful sips during breaks to reduce stress",
        "📚 Coffee can improve memory consolidation when studying",
        "⚡ Small frequent coffee doses work better than one large cup",
        "🔄 Take a coffee break every 90 minutes to maintain focus",
        "🌅 Morning coffee helps establish a productive daily routine",
        
        // 🌍 Coffee Culture & History
        "🌍 Coffee was first discovered in Ethiopia around 850 AD",
        "🇮🇹 Espresso means 'pressed out' in Italian",
        "☕ There are over 800 flavor compounds in coffee",
        "🏔️ High altitude coffee beans have more complex flavors",
        "🌿 Arabica beans make up 60% of world coffee production",
        "🔥 Light roasts have more caffeine than dark roasts",
        "🇯🇵 Japanese pour-over method emphasizes precision and patience",
        "🇹🇷 Turkish coffee is the oldest brewing method still in use",
        "💎 The most expensive coffee comes from civet cat droppings",
        "🌊 Cold brew has 67% less acid than hot coffee"
    )
    
    val currentTip = remember { tips.random() }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Coffee Tip of the Day",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD2691E),
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(1f, 1f),
                        blurRadius = 3f
                    )
                )
            )
            
            Text(
                text = currentTip,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 14.sp,
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

// 3. Quick Stats Section
@Composable
fun QuickStatsSection(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickStatCard(
            title = "Daily Avg",
            value = "4.2",
            unit = "cups",
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            title = "Best Streak",
            value = "12",
            unit = "days",
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            title = "Total",
            value = "347",
            unit = "cups",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f),
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(1f, 1f),
                        blurRadius = 2f
                    )
                )
            )
            Text(
                text = value,
                fontSize = 14.sp,
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
                text = unit,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.6f),
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