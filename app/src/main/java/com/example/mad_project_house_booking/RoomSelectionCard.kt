package com.example.mad_project_house_booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoomSelectionCard(
    roomName: String,
    price: String,
    isAvailable: Boolean,
    imageResId: Int, // Image resource ID
    onBookClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp), // Rounded edges
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)) // Clipping to avoid overflow
            .background(Color.LightGray), // Gray background
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.DarkGray) // Border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Room Image
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Room Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) // Clip image corners
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = roomName, fontSize = 20.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Price: BDT$price / night", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAvailable) "Available" else "Not Available",
                fontSize = 14.sp,
                color = if (isAvailable) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onBookClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = isAvailable,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAvailable) Color.Blue else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Book Now")
            }}
}}