package com.remziakgoz.coffeepomodoro.presentation.dashboard.views

import android.text.Layout
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.components.CoffeeProgressCard

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {

    val successText by remember { mutableStateOf("Espresso Master\nCups Drank") }
    val days = listOf("W", "T", "W", "T", "F", "S", "S")

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

            Box {
                Row(
                    modifier = modifier.padding(start = 5.dp, end = 5.dp)
                ) {
                    CoffeeProgressCard(
                        counter = 3,
                        imageId = R.drawable.cup1fordb,
                        dayProgress = "Today"
                    )
                    Spacer(modifier = modifier.padding(5.dp))
                    CoffeeProgressCard(
                        counter = 15,
                        imageId = R.drawable.cup7fordb,
                        dayProgress = "Week"
                    )
                    Spacer(modifier = modifier.padding(5.dp))
                    CoffeeProgressCard(
                        counter = 54,
                        imageId = R.drawable.cupcorefordb2,
                        dayProgress = "Month"
                    )

                }
            }
            Spacer(modifier = modifier.size(80.dp))

            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    days.forEachIndexed { index, day ->
                        Box(
                            modifier = modifier
                                .padding(4.dp)
                                .padding(8.dp)
                        ) {
                            Box(

                            ) {

                            }
                            Text(
                                text = day,
                                fontSize = 36.sp,
                                color = Color.White.copy(
                                    alpha = 0.8f
                                ),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }
            }

        }

    }


}

@Preview(showBackground = true)
@Composable
fun Preview(modifier: Modifier = Modifier) {
    DashboardScreen()
}