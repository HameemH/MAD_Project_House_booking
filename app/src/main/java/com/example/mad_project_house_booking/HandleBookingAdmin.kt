package com.example.mad_project_house_booking

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HandleBooking() {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var schedules by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var selectedScheduleId by remember { mutableStateOf<String?>(null) }
    var rejectionNote by remember { mutableStateOf(TextFieldValue()) }

    fun removeFromUI(scheduleId: String) {
        schedules = schedules.filterNot { it["id"] == scheduleId }
    }

    LaunchedEffect(true) {
        firestore.collection("schedules")
            .whereEqualTo("adminNotification", true)
            .get()
            .addOnSuccessListener { snapshot ->
                schedules = snapshot.documents.map { doc ->
                    val data = doc.data ?: emptyMap()
                    data + ("id" to doc.id)
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn {
            items(schedules) { schedule ->
                HandleBookingCard(
                    schedule = schedule,
                    onAcceptSchedule = {
                        val scheduleId = schedule["id"] as String
                        firestore.collection("schedules").document(scheduleId)
                            .update(
                                mapOf(
                                    "scheduleStatus" to true,
                                    "adminNotification" to false,
                                    "userNotification" to true
                                )
                            )
                        Toast.makeText(context, "Schedule accepted", Toast.LENGTH_SHORT).show()
                        removeFromUI(scheduleId)
                    },
                    onRejectSchedule = {
                        selectedScheduleId = schedule["id"] as String
                        showNoteDialog = true
                    },
                    onAcceptBooking = {
                        val propertyId = schedule["propertyId"] as String
                        val scheduleId = schedule["id"] as String
                        val userId = schedule["userId"] as String

                        firestore.collection("schedules").document(scheduleId)
                            .update(
                                mapOf(
                                    "scheduleStatus" to true,
                                    "adminNotification" to false,
                                    "userNotification" to true
                                )
                            )

                        firestore.collection("properties").document(propertyId)
                            .update("isAvailable", false)

                        val booking = hashMapOf(
                            "propertyId" to propertyId,
                            "userId" to userId,
                            "timestamp" to System.currentTimeMillis()
                        )
                        firestore.collection("confirmedBookings")
                            .add(booking)

                        Toast.makeText(context, "Booking accepted and recorded", Toast.LENGTH_SHORT).show()
                        removeFromUI(scheduleId)
                    },
                    onRejectBooking = {
                        selectedScheduleId = schedule["id"] as String
                        showNoteDialog = true
                    }
                )
            }
        }
    }

    if (showNoteDialog && selectedScheduleId != null) {
        AlertDialog(
            onDismissRequest = {
                showNoteDialog = false
                selectedScheduleId = null
            },
            title = { Text("Rejection Note") },
            text = {
                OutlinedTextField(
                    value = rejectionNote,
                    onValueChange = { rejectionNote = it },
                    label = { Text("Enter reason") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    firestore.collection("schedules").document(selectedScheduleId!!)
                        .update(
                            mapOf(
                                "scheduleStatus" to false,
                                "adminNote" to rejectionNote.text,
                                "adminNotification" to false,
                                "userNotification" to true,
                                "rejected" to true
                            )
                        )
                    Toast.makeText(context, "Rejected with note", Toast.LENGTH_SHORT).show()
                    removeFromUI(selectedScheduleId!!)
                    showNoteDialog = false
                    selectedScheduleId = null
                    rejectionNote = TextFieldValue()
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNoteDialog = false
                    selectedScheduleId = null
                    rejectionNote = TextFieldValue()
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

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
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("User ID: ${schedule["userId"]}")
            Text("Property ID: ${schedule["propertyId"]}")
            Text("Date: ${schedule["scheduleDate"]}")
            Text("Time: ${schedule["scheduleTime"]}")

            val bookingRequest = schedule["bookingRequest"] as? Boolean ?: false
            Spacer(modifier = Modifier.height(8.dp))

            if (bookingRequest) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAcceptBooking) {
                        Text("Accept Booking")
                    }
                    Button(onClick = onRejectBooking) {
                        Text("Reject Booking")
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAcceptSchedule) {
                        Text("Accept Schedule")
                    }
                    Button(onClick = onRejectSchedule) {
                        Text("Reject Schedule")
                    }
                }
            }
        }
    }
}
