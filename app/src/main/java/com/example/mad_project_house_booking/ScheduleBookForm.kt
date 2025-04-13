package com.example.mad_project_house_booking

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBookForm(propertyId: String, navController: NavHostController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var bookingRequest by remember { mutableStateOf(false) }
    var alreadyRequested by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        firestore.collection("schedules")
            .whereEqualTo("userId", uid)
            .whereEqualTo("propertyId", propertyId)
            .get()
            .addOnSuccessListener { snapshot ->
                alreadyRequested = snapshot.documents.any { doc ->
                    val status = doc.getBoolean("scheduleStatus") ?: false
                    val rejected = doc.getBoolean("rejected") ?: false
                    !status && !rejected
                }
            }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFeef2f3), Color(0xFF8e9eab))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule/Booking Request") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.LightGray
    ) { paddingValues ->
        if (alreadyRequested) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(gradientBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "You've already submitted a request for this property.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.ui.graphics.Color.DarkGray
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(gradientBackground)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = { selectedDate = it },
                    label = { Text("Write Your preferred Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = { selectedTime = it },
                    label = { Text("Preferred Time (e.g. 10:00 AM - 11:00 AM)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = bookingRequest,
                        onCheckedChange = { bookingRequest = it }
                    )
                    Text("Request to Book Property")
                }

                Button(
                    onClick = {
                        val data = hashMapOf(
                            "userId" to uid,
                            "propertyId" to propertyId,
                            "scheduleDate" to selectedDate,
                            "scheduleTime" to selectedTime,
                            "scheduleStatus" to false,
                            "bookingRequest" to bookingRequest,
                            "adminNotification" to true,
                            "userNotification" to false
                        )

                        firestore.collection("schedules")
                            .add(data)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Request submitted!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Request")
                }
            }
        }
    }
}
