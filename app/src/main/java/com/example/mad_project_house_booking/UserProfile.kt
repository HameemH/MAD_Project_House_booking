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

    var userSchedules by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var schedulePropertyNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

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

            userSchedules = schedulesSnapshot.documents.map { it.data ?: emptyMap() }

            // Fetch all property names for schedules
            val propertyIds = userSchedules.mapNotNull { it["propertyId"] as? String }.distinct()
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

    // UI - Keep your existing background and layout structure
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
            // 1. User Profile Card (keep your existing UI)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Editable Name
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

                    // Editable Contact
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

            // 2. Booked Property Section (with actual property name)
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

            // 3. Requests Section with property names
            Text("📅 Your Requests:",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White)

            if (userSchedules.isEmpty()) {
                Text("No booking requests yet", color = Color.White)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(userSchedules) { schedule ->
                        val propertyId = schedule["propertyId"] as? String ?: ""
                        val propertyName = schedulePropertyNames[propertyId] ?: "Unknown Property"

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
                                Text("ID: $propertyId")
                                Text("Date: ${schedule["scheduleDate"]}")
                                Text("Time: ${schedule["scheduleTime"]}")

                                Spacer(modifier = Modifier.height(8.dp))

                                when {
                                    schedule["scheduleStatus"] as? Boolean == true ->
                                        Text("✅ Accepted", color = Color(0xFF2E7D32))
                                    schedule["rejected"] as? Boolean == true -> {
                                        Text("❌ Rejected: ${schedule["adminNote"]}",
                                            color = Color(0xFFD32F2F))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(onClick = { /* Handle reschedule */ }) {
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
}

