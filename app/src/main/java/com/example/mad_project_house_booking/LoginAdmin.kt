package com.example.mad_project_house_booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun LoginAdmin(navController:NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") } // Default role


    //Total front end
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Set background to White
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Log In",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(text = "Email", modifier = Modifier.align(Alignment.Start), color = Color.Black)
        BasicTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray) // Gray underline
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Password", modifier = Modifier.align(Alignment.Start), color = Color.Black)
        BasicTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray) // Gray underline
        )






        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate("userLanding")
        }) {
            Text(text = "Login", color = Color.White) // White text on button
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Are you a User? ")
            Text(
                text = "User Login",
                color = MaterialTheme.colorScheme.primary, // Using theme's primary color as link
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable{
                    navController.navigate("login") // Navigate to Registration.kt
                }
            )

        }

    }
}
