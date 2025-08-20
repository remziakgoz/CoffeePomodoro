package com.remziakgoz.coffeepomodoro.presentation.auth.views

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.remziakgoz.coffeepomodoro.R
import com.remziakgoz.coffeepomodoro.presentation.auth.AuthViewModel
import com.remziakgoz.coffeepomodoro.presentation.components.GooeySlimeLoader
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.Pacifico
import com.remziakgoz.coffeepomodoro.presentation.ui.theme.signInColor
import kotlinx.coroutines.launch


@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onNavigateBack: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken != null) {
                scope.launch {
                    viewModel.signInWithGoogle(idToken)
                        .onSuccess { onNavigateBack() }
                        .onFailure {
                            Toast.makeText(
                                context,
                                it.localizedMessage ?: "Google sign-in failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Google sign-in failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }

    }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.welcome),
            contentDescription = "Sign In Background Image",
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Back button in top left
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = "Sign in",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                style = Pacifico,
                fontSize = 40.sp
            )

            Spacer(modifier = modifier.padding(5.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                enabled = !isLoading,
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.mailicon),
                        contentDescription = "Email Icon"
                    )
                },
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
                enabled = !isLoading,
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.passwordicon),
                        contentDescription = "Password Icon"
                    )
                },
                trailingIcon = {
                    val visibilityIcon = if (passwordVisible)
                        R.drawable.passwordhideicon
                    else
                        R.drawable.passwordshowicon
                    IconButton(
                        enabled = !isLoading,
                        onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = visibilityIcon),
                            contentDescription = "Password Toggle"
                        )
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

            Spacer(modifier = modifier.padding(8.dp))

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Enter email and password!", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        scope.launch {
                            isLoading = true
                            try {
                                viewModel.signIn(email, password)
                                    .onSuccess { onNavigateBack() }
                                    .onFailure {
                                        Toast.makeText(
                                            context,
                                            it.localizedMessage ?: "Unknown error occurred",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            } finally {
                                isLoading = false
                            }

                        }
                    }
                }, modifier = modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                    containerColor = signInColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 18.sp,
                    style = Pacifico
                )
            }
            Spacer(modifier = modifier.padding(5.dp))

            Button(
                onClick = {
                    isLoading = true
                    launcher.launch(googleSignInClient.signInIntent)
                },
                enabled = !isLoading,
                modifier = modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable._123025_logo_google_g_icon),
                        contentDescription = "Google Icon",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = modifier.padding(5.dp))
                    Text(
                        text = "Sign in with Google",
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }


            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {

                Text(
                    "Don't have an Account?",
                    color = Color.Black.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )
                TextButton(onClick = { if (!isLoading) onNavigateToSignUp() },
                    enabled = !isLoading) {
                    Text("Sign Up", color = signInColor, fontSize = 14.sp)
                }

            }

        }
        if (isLoading) {
            Box(
                Modifier
                    .matchParentSize()
                    .zIndex(1f)
                    .background(Color.Black.copy(alpha = 0.12f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* no-op, clicks are consumed */ },
                contentAlignment = Alignment.Center
            ) {
                GooeySlimeLoader()
            }
        }
    }
}