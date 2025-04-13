package com.example.mad_project_house_booking

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserProfile(navController: NavHostController, authViewModel: AuthViewModel) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var userName by remember { mutableStateOf("") }
    var userSchedules by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var bookedPropertyId by remember { mutableStateOf<String?>(null) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var selectedScheduleId by remember { mutableStateOf<String?>(null) }
    var newDate by remember { mutableStateOf(TextFieldValue()) }
    var newTime by remember { mutableStateOf(TextFieldValue()) }
    var newBookingRequest by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                userName = doc.getString("name") ?: "User"
            }

        firestore.collection("confirmedBookings")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.documents.isNotEmpty()) {
                    bookedPropertyId = snapshot.documents.first().getString("propertyId")
                }
            }

        firestore.collection("schedules")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                userSchedules = snapshot.documents.map { doc ->
                    val data = doc.data ?: emptyMap()
                    data + ("id" to doc.id)
                }
            }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp)
    ) {
        Text("Welcome, $userName", style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        bookedPropertyId?.let {
            Text("\uD83C\uDFE0 You have booked property: $it", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("\uD83D\uDCC5 Your Requests:", style = MaterialTheme.typography.titleMedium, color = Color.White)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(userSchedules) { schedule ->
                val status = schedule["scheduleStatus"] as? Boolean ?: false
                val rejected = schedule["rejected"] as? Boolean ?: false
                val note = schedule["adminNote"] as? String

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Property ID: ${schedule["propertyId"]}", color = Color.Black)
                        Text("Date: ${schedule["scheduleDate"]}", color = Color.Black)
                        Text("Time: ${schedule["scheduleTime"]}", color = Color.Black)

                        Spacer(modifier = Modifier.height(8.dp))

                        if (status) {
                            Text("✅ Accepted", color = Color(0xFF2E7D32))
                        } else if (rejected) {
                            Text("❌ Rejected: $note", color = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(onClick = {
                                selectedScheduleId = schedule["id"] as String
                                showRescheduleDialog = true
                            }) {
                                Text("Reschedule")
                            }
                        } else {
                            Text("⌛ Pending Review", color = Color(0xFFF9A825))
                        }
                    }
                }
            }
        }
    }

    if (showRescheduleDialog && selectedScheduleId != null) {
        AlertDialog(
            onDismissRequest = {
                showRescheduleDialog = false
                selectedScheduleId = null
                newDate = TextFieldValue()
                newTime = TextFieldValue()
                newBookingRequest = false
            },
            title = { Text("Reschedule Visit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newDate,
                        onValueChange = { newDate = it },
                        label = { Text("New Date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTime,
                        onValueChange = { newTime = it },
                        label = { Text("New Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = newBookingRequest,
                            onCheckedChange = { newBookingRequest = it }
                        )
                        Text("Request to Book")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    firestore.collection("schedules").document(selectedScheduleId!!)
                        .update(
                            mapOf(
                                "scheduleDate" to newDate.text,
                                "scheduleTime" to newTime.text,
                                "bookingRequest" to newBookingRequest,
                                "adminNotification" to true,
                                "userNotification" to false,
                                "rejected" to false
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(context, "Rescheduled successfully", Toast.LENGTH_SHORT).show()
                            showRescheduleDialog = false
                            selectedScheduleId = null
                            newDate = TextFieldValue()
                            newTime = TextFieldValue()
                        }
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRescheduleDialog = false
                    selectedScheduleId = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}