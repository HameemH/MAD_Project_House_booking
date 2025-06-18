package com.example.mad_project_house_booking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HandleBookingCard(
    schedule: Map<String, Any>,
    onAcceptSchedule: () -> Unit,
    onRejectSchedule: () -> Unit,
    onAcceptBooking: () -> Unit,
    onRejectBooking: () -> Unit
) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("User ID: ${schedule["userId"]}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Property ID: ${schedule["propertyId"]}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text("Date: ${schedule["scheduleDate"]}", style = MaterialTheme.typography.bodyMedium)
            Text("Time: ${schedule["scheduleTime"]}", style = MaterialTheme.typography.bodyMedium)

            val bookingRequest = schedule["bookingRequest"] as? Boolean ?: false
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = if (bookingRequest) onAcceptBooking else onAcceptSchedule,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (bookingRequest) "Accept Booking" else "Accept Schedule")
                }
                OutlinedButton(
                    onClick = if (bookingRequest) onRejectBooking else onRejectSchedule,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (bookingRequest) "Reject Booking" else "Reject Schedule")
                }
            }
        }
    }
}