package com.example.mad_project_house_booking.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mad_project_house_booking.components.PropertyRequestCard
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PropertyRequestsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var propertyRequests by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            firestore.collection("rproperties")
                .whereEqualTo("requestStatus", false)
                .get()
                .addOnSuccessListener { snapshot ->
                    propertyRequests = snapshot.documents.map { doc ->
                        doc.data?.plus("id" to doc.id) ?: emptyMap()
                    }
                    isLoading = false
                }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading requests", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    fun removeRequest(requestId: String) {
        firestore.collection("rproperties").document(requestId)
            .update("requestStatus", true)
            .addOnSuccessListener {
                propertyRequests = propertyRequests.filterNot { it["id"] == requestId }
            }
    }

    fun approveProperty(request: Map<String, Any>) {
        val propertyData = hashMapOf(
            "location" to request["location"],
            "houseName" to request["houseName"],
            "houseType" to request["houseType"],
            "roomDetails" to request["roomDetails"],
            "rent" to request["rent"],
            "facilities" to request["facilities"],
            "description" to request["description"],
            "isAvailable" to true,
            "img1" to request["img1"],
            "img2" to request["img2"],
            "img3" to request["img3"]
        )

        val docId = request["id"] as? String ?: return

        firestore.collection("properties").add(propertyData)
            .addOnSuccessListener {
                firestore.collection("rproperties").document(docId)
                    .update("requestStatus", true)
                    .addOnSuccessListener {
                        removeRequest(docId)
                        Toast.makeText(context, "Property approved and listed", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Approval failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Property Approval Requests",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                propertyRequests.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No pending requests")
                }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(propertyRequests) { request ->
                        PropertyRequestCard(
                            name = request["houseName"] as? String ?: "Unnamed Property",
                            location = request["location"] as? String ?: "Unknown Location",
                            onReject = { removeRequest(request["id"] as String) },
                            onViewDetails = {
                                navController.navigate("reqeustdetails/${request["id"]}")
                            },
                            onApprove = { approveProperty(request) }
                        )
                    }
                }
            }
        }
    }
}

