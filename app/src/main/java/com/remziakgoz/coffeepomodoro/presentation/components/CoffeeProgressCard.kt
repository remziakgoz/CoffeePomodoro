package com.remziakgoz.coffeepomodoro.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.CoffeePrimaryLight

@Composable
fun CoffeeProgressCard(modifier: Modifier = Modifier, counter: Int, imageId: Int) {

    Card(
        modifier = modifier
            .size(width = 180.dp, height = 300.dp)
            .clip(RoundedCornerShape(32.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.background(color = Color(0xFFF5E5DA)).padding(16.dp)

        ) {
            Text(
                text = "Today",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                color = CoffeePrimaryLight.copy(
                    alpha = 0.8f
                )
            )
            Spacer(modifier = modifier.padding(5.dp))
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Coffee Cup"
            )
            Spacer(modifier = modifier.padding(5.dp))
            Text(
                text = "$counter",
                fontSize = 50.sp,
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
    CoffeeProgressCard(counter = 3, imageId = R.drawable.cup1fordb)

}