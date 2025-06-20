package com.example.mad_project_house_booking.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mad_project_house_booking.data_util.AuthState
import com.example.mad_project_house_booking.data_util.AuthViewModel
import com.example.mad_project_house_booking.R

@Composable
fun LoginPage(navController: NavHostController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Authenticated -> navController.navigate("userLanding")
            is AuthState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A2980),  // Dark Blue
            Color(0xFF26D0CE)   // Teal/Cyan
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.premier_logo),
                    contentDescription = "PremierHouse Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = "PremierHouse",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
                        ),

                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Login",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )


                InputField(
                    label = "Email Address",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Enter your email"
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true,
                    placeholder = "Enter your password"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { authViewModel.login(email, password) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A2980)
                    ),
                    shape = RoundedCornerShape(50),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }


                Spacer(modifier = Modifier.height(20.dp))

                AccountPrompt(
                    prompt = "Don't have an account?",
                    actionText = "Sign Up",
                    onClick = { navController.navigate("registration") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                AccountPrompt(
                    prompt = "Are you an admin?",
                    actionText = "Admin Login",
                    onClick = { navController.navigate("adminLogin") }
                )
                Spacer(modifier = Modifier.height(12.dp))

                AccountPrompt(
                    prompt = "Wanna Check As guest?",
                    actionText = "Guest Login",
                    onClick = { navController.navigate("guestLanding") }
                )
            }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.DarkGray,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(10.dp))
                .border(BorderStroke(1.dp, Color.LightGray), shape = RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun AccountPrompt(prompt: String, actionText: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = prompt, color = Color.DarkGray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = actionText,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}
