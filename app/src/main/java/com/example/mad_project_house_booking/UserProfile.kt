package com.example.mad_project_house_booking

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
data class Schedule(
    val firestoreId: String,
    val propertyId: String,
    val userId: String,
    val scheduleDate: String,
    val scheduleTime: String,
    val scheduleStatus: Boolean?,
    val rejected: Boolean?,
    val adminNote: String?,
    val bookingRequest: Boolean?
) {
    constructor() : this("", "", "", "", "", null, null, null, null)

    companion object {
        fun fromMap(map: Map<String, Any>): Schedule {
            return Schedule(
                firestoreId = map["firestoreId"] as? String ?: "",
                propertyId = map["propertyId"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                scheduleDate = map["scheduleDate"] as? String ?: "",
                scheduleTime = map["scheduleTime"] as? String ?: "",
                scheduleStatus = map["scheduleStatus"] as? Boolean,
                rejected = map["rejected"] as? Boolean,
                adminNote = map["adminNote"] as? String,
                bookingRequest = map["bookingRequest"] as? Boolean
            )
        }
    }
}
@Composable
fun UserProfile(navController: NavHostController, authViewModel: AuthViewModel) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    // State variables
    var userName by remember { mutableStateOf("") }
    var userContact by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf<Pair<String, String>?>(null) }
    var tempEditValue by remember { mutableStateOf("") }

    var bookedProperty by remember { mutableStateOf<Map<String, Any>?>(null) }
    var bookedPropertyName by remember { mutableStateOf("") }
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var showLeaveHouseDialog by remember { mutableStateOf(false) }

    var userSchedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var schedulePropertyNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    var showRescheduleDialog by remember { mutableStateOf(false) }
    var selectedScheduleId by remember { mutableStateOf<String?>(null) }
    var newDate by remember { mutableStateOf(TextFieldValue()) }
    var newTime by remember { mutableStateOf(TextFieldValue()) }
    var newBookingRequest by remember { mutableStateOf(false) }

    // Fetch data
    LaunchedEffect(uid) {
        try {
            // 1. Fetch user profile
            val userDoc = firestore.collection("users").document(uid).get().await()
            userName = userDoc.getString("name") ?: "User"
            userContact = userDoc.getString("contact") ?: ""

            // 2. Fetch booked property with name
            val bookingsSnapshot = firestore.collection("confirmedBookings")
                .whereEqualTo("userId", uid)
                .get()
                .await()

            if (!bookingsSnapshot.isEmpty) {
                val booking = bookingsSnapshot.documents.first()
                bookedProperty = booking.data ?: emptyMap()

                // Fetch property name
                val propertyId = booking.getString("propertyId") ?: ""
                val propertyDoc = firestore.collection("properties").document(propertyId).get().await()
                bookedPropertyName = propertyDoc.getString("houseName") ?: "Unknown Property"
            }

            // 3. Fetch schedules with property names
            val schedulesSnapshot = firestore.collection("schedules")
                .whereEqualTo("userId", uid)
                .get()
                .await()

            userSchedules = schedulesSnapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data?.toMutableMap() ?: mutableMapOf()
                    data["firestoreId"] = doc.id
                    Schedule.fromMap(data)
                } catch (e: Exception) {
                    null
                }
            }

            // Fetch all property names for schedules
            val propertyIds = userSchedules.map { it.propertyId }.distinct()
            val namesMap = mutableMapOf<String, String>()
            propertyIds.forEach { id ->
                val doc = firestore.collection("properties").document(id).get().await()
                namesMap[id] = doc.getString("houseName") ?: "Unknown Property"
            }
            schedulePropertyNames = namesMap

        } catch (e: Exception) {
            Toast.makeText(context, "Error loading data", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
                )
            )
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // 1. User Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Name: $userName", style = MaterialTheme.typography.bodyLarge)
                        IconButton(
                            onClick = {
                                editingField = "name" to userName
                                tempEditValue = userName
                                showEditDialog = true
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Name")
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Contact: $userContact", style = MaterialTheme.typography.bodyLarge)
                        IconButton(
                            onClick = {
                                editingField = "contact" to userContact
                                tempEditValue = userContact
                                showEditDialog = true
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Contact")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Booked Property Section
            bookedProperty?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🏠 You have booked: $bookedPropertyName",
                            style = MaterialTheme.typography.titleMedium)
                        Text("Property ID: ${it["propertyId"]}")

                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            Button(
                                onClick = { showReviewDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Write Review")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { showLeaveHouseDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Leave House")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Requests Section
            Text("📅 Your Requests:",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White)

            if (userSchedules.isEmpty()) {
                Text("No booking requests yet", color = Color.White)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(userSchedules) { schedule ->
                        val propertyName = schedulePropertyNames[schedule.propertyId] ?: "Unknown Property"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(propertyName, style = MaterialTheme.typography.titleSmall)
                                Text("ID: ${schedule.propertyId}")
                                Text("Date: ${schedule.scheduleDate}")
                                Text("Time: ${schedule.scheduleTime}")
                                Text("Schedule ID: ${schedule.firestoreId}")

                                Spacer(modifier = Modifier.height(8.dp))

                                when {
                                    schedule.scheduleStatus == true ->
                                        Text("✅ Accepted", color = Color(0xFF2E7D32))
                                    schedule.rejected == true -> {
                                        Text("❌ Rejected: ${schedule.adminNote}",
                                            color = Color(0xFFD32F2F))
                                        Spacer(modifier = Modifier.height(4.dp))

                                        Button(onClick = {
                                            selectedScheduleId = schedule.firestoreId
                                            showRescheduleDialog = true
                                        }) {
                                            Text("Reschedule")
                                        }
                                    }
                                    else -> Text("⌛ Pending Review", color = Color(0xFFF9A825))
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    // Dialogs (keep your existing implementations)
    // 1. Edit Dialog
    if (showEditDialog && editingField != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit ${editingField?.first?.capitalize()}") },
            text = {
                OutlinedTextField(
                    value = tempEditValue,
                    onValueChange = { tempEditValue = it },
                    label = { Text("New ${editingField?.first}") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    when (editingField?.first) {
                        "name" -> userName = tempEditValue
                        "contact" -> userContact = tempEditValue
                    }
                    firestore.collection("users").document(uid)
                        .update(editingField?.first!!, tempEditValue)
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Review Dialog
    if (showReviewDialog && bookedProperty != null) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Write Review for $bookedPropertyName") },
            text = {
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Your review") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            },
            confirmButton = {
                Button(onClick = {
                    firestore.collection("reviews").add(
                        hashMapOf(
                            "userId" to uid,
                            "propertyId" to bookedProperty?.get("propertyId"),
                            "propertyName" to bookedPropertyName,
                            "review" to reviewText,

                        )
                    )
                    showReviewDialog = false
                    reviewText = ""
                    Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. Leave House Dialog
    if (showLeaveHouseDialog && bookedProperty != null) {
        AlertDialog(
            onDismissRequest = { showLeaveHouseDialog = false },
            title = { Text("Leave $bookedPropertyName?") },
            text = { Text("Are you sure you want to leave this property?") },
            confirmButton = {
                Button(
                    onClick = {
                        val propertyId = bookedProperty?.get("propertyId") as? String ?: return@Button

                        // 1. Update property availability
                        firestore.collection("properties").document(propertyId)
                            .update("isAvailable", true)

                        // 2. Delete booking record
                        firestore.collection("confirmedBookings")
                            .whereEqualTo("propertyId", propertyId)
                            .whereEqualTo("userId", uid)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                snapshot.documents.forEach { it.reference.delete() }
                                bookedProperty = null
                                showLeaveHouseDialog = false
                                Toast.makeText(context, "Property left successfully", Toast.LENGTH_SHORT).show()
                            }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirm Leave")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveHouseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
                    selectedScheduleId?.let { sid ->
                        firestore.collection("schedules").document(sid)
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

