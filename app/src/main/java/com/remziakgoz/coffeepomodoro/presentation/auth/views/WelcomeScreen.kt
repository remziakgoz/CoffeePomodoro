package com.remziakgoz.coffeepomodoro.presentation.auth.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.Pacifico

@Composable
fun WelcomeScreen(
    modifier: Modifier, 
    innerPadding: PaddingValues,
    onContinue: () -> Unit
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.welcome),
            contentDescription = "Welcome Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .padding(bottom = 150.dp)
        ) {
            Text(
                text = "Welcome",
                style = Pacifico,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "A minimalist Pomodoro app for coffee lovers.\n" + "Stay sharp, stay caffeinated",
                color = Color.Black.copy(alpha = 0.4f),
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { 
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > 500) {
                    lastClickTime = currentTime
                    onContinue()
                }
            }, 
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = Color.Gray
            ), modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Continue",
                    color = Color.Black.copy(
                        alpha = 0.4f
                    ),
                    fontSize = 16.sp,
                    style = Pacifico
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Image(
                    painter = painterResource(id = R.drawable.welcomebutton),
                    contentDescription = "Welcome Button",
                    modifier = Modifier.size(47.dp)
                )
            }

        }

    }

}