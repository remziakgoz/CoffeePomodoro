package com.remziakgoz.coffeepomodoro.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.Pacifico
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.signInColor

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, innerPadding: PaddingValues) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.welcome),
            contentDescription = "Sign Up Background Image",
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier
                .align(alignment = Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "Sign Up",
                fontWeight = FontWeight.Bold,
                style = Pacifico,
                fontSize = 40.sp
            )

            Spacer(modifier = modifier.padding(5.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email")},
                leadingIcon = { Icon(painterResource(R.drawable.mailicon), contentDescription = "Email Icon") },
                placeholder = { Text("Please enter your email") },
                singleLine = true,
                modifier = modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = signInColor,
                    unfocusedIndicatorColor = Color(0xFFFFCC80),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = modifier.padding(5.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(painterResource(R.drawable.passwordicon), contentDescription = "Password Icon") },
                trailingIcon = {
                    val visibilityIcon = if(passwordVisible)
                        R.drawable.passwordhideicon
                    else
                        R.drawable.passwordshowicon
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = painterResource(id = visibilityIcon), contentDescription = "Password Toggle")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                placeholder = { Text("Please enter your password") },
                singleLine = true,
                modifier = modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = signInColor,
                    unfocusedIndicatorColor = Color(0xFFFFCC80),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = modifier.padding(32.dp))

            Button(onClick = { /*TODO*/ }, modifier = modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                containerColor = signInColor,
                contentColor = Color.White
            )) {
                Text(
                    text = "Sign Up",
                    color = Color.White,
                    fontSize = 18.sp,
                    style = Pacifico
                )
            }
            Spacer(modifier = modifier.padding(5.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                Text("Already have an Account?", color = Color.Black.copy(alpha = 0.4f), fontSize = 14.sp)
                TextButton(onClick = { /*TODO*/ }) {
                    Text("Sign In", color = signInColor, fontSize = 14.sp)
                }

            }
        }
    }
}