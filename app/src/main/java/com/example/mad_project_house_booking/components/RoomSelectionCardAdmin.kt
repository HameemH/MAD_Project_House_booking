package com.example.mad_project_house_booking.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun RoomSelectionCardAdmin(
    roomName: String,
    price: String,
    isAvailable: Boolean,
    imageUrls: List<String>,
    onUpdateClick: () -> Unit,
    onDetailsClick: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.DarkGray)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

            ImageSliderWithIndicators(imageUrls)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = roomName, fontSize = 20.sp, color = Color.Black, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Price: BDT $price / Month", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAvailable) "Available" else "Not Available",
                fontSize = 14.sp,
                color = if (isAvailable) Color.Green else Color.Red
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onUpdateClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color.Blue)
            ) {
                Text("Update", color = Color.Blue)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDetailsClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Text("Delete", color = Color.Red)
            }
        }
    }
}




