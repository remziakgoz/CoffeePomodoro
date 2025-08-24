package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CoffeePrimaryLight
import com.remziakgoz.coffeepomodoro.presentation.root.isLandscape

@Composable
fun CoffeeProgressCard(modifier: Modifier = Modifier, counter: Int, imageId: Int, dayProgress : String) {
    val isLandscapeMode = isLandscape()
    val cardSize = if (isLandscapeMode) 160.dp to 240.dp else 120.dp to 200.dp
    val imageSize = if (isLandscapeMode) 80.dp else null
    val titleFontSize = if (isLandscapeMode) 24.sp else 30.sp
    val counterFontSize = if (isLandscapeMode) 32.sp else 25.sp
    val cardPadding = if (isLandscapeMode) 20.dp else 16.dp

    Card(
        modifier = modifier
            .size(width = cardSize.first, height = cardSize.second)
            .clip(RoundedCornerShape(32.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .background(color = Color(0xFFE3CBA5))
                .padding(cardPadding)

        ) {
            Text(
                text = dayProgress,
                textAlign = TextAlign.Center,
                fontSize = titleFontSize,
                fontWeight = FontWeight.W400,
                color = CoffeePrimaryLight.copy(
                    alpha = 0.8f
                )
            )
            Spacer(modifier = modifier.padding(5.dp))
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Coffee Cup",
                modifier = if (imageSize != null) modifier.size(imageSize) else modifier
            )
            Spacer(modifier = modifier.padding(5.dp))
            Text(
                text = "$counter",
                fontSize = counterFontSize,
                fontWeight = FontWeight.Bold,
                color = CoffeePrimaryLight.copy(
                    alpha = 0.8f
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview(modifier: Modifier = Modifier) {
    CoffeeProgressCard(counter = 3, imageId = R.drawable.cup5fordb, dayProgress = "Today")

}