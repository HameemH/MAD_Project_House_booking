package com.example.mad_project_house_booking.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun Rules(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "User Guidelines",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Description
        Text(
            text = "Please take a moment to read our guidelines. These are designed to make your experience better and safer.",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Scrollable List of Guidelines
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(guidelines) { guideline ->
                GuidelineCard(guideline)
            }
        }

        // Close Button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text("I Agree", fontSize = 16.sp)
        }
    }
}

@Composable
fun GuidelineCard(guideline: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = guideline,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
            )
        }
    }
}

// Sample guidelines
val guidelines = listOf(
    "1. Be respectful to other users.",
    "2. Always provide accurate information.",
    "3. Ensure that the property details are correct.",
    "4. Do not share personal contact information with strangers.",
    "5. Report any suspicious activity to the app administrators.",
    "6. Follow the payment instructions and timelines.",
    "7. Review and rate properties honestly after your visit.",
    "8. Respect the property owner's rules and regulations."
)
