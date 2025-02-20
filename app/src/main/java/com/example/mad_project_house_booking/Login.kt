package com.example.mad_project_house_booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoginPage() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") } // Default role

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Set background to White
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        // Radio buttons for Admin and User
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Admin", color = Color.Black)
            RadioButton(
                selected = selectedRole == "Admin",
                onClick = { selectedRole = "Admin" },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = "User", color = Color.Black)
            RadioButton(
                selected = selectedRole == "User",
                onClick = { selectedRole = "User" },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            // Handle login logic here
        }) {
            Text(text = "Login", color = Color.White) // White text on button
            }
    }
}