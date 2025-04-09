package com.example.mad_project_house_booking

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UpdateProperty(propertyId: String, navController: NavHostController) {
    val firestore = FirebaseFirestore.getInstance()
    var property by remember { mutableStateOf<Room?>(null) }
    val context = LocalContext.current

    LaunchedEffect(propertyId) {
        firestore.collection("properties").document(propertyId).get()
            .addOnSuccessListener { doc ->
                property = Room(
                    houseName = doc.getString("houseName") ?: "",
                    rent = doc.getString("rent") ?: "",
                    isAvailable = true,
                    img1 = doc.getString("img1") ?: "",
                    img2 = doc.getString("img2") ?: "",
                    img3 = doc.getString("img3") ?: "",
                    location = doc.getString("location") ?: "",
                    roomDetails = doc.getString("roomDetails") ?: "",
                    facilities = doc.getString("facilities") ?: "",
                    description = doc.getString("description") ?: "",
                    houseType = doc.getString("houseType") ?:""

                )
            }
    }

    property?.let { room ->
        val images = listOf(room.img1, room.img2, room.img3)
        val pagerState = rememberPagerState(pageCount = { images.size })

        // Auto-scroll
        LaunchedEffect(pagerState) {
            while (true) {
                delay(3000)
                val next = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(next)
            }
        }

        var houseName by remember { mutableStateOf(room.houseName) }
        var rent by remember { mutableStateOf(room.rent) }
        var location by remember { mutableStateOf(room.location) }
        var roomDetails by remember { mutableStateOf(room.roomDetails) }
        var facilities by remember { mutableStateOf(room.facilities) }
        var description by remember { mutableStateOf(room.description) }
        var img1 by remember { mutableStateOf(room.img1) }
        var img2 by remember { mutableStateOf(room.img2) }
        var img3 by remember { mutableStateOf(room.img3) }
        var houseType by remember { mutableStateOf(room.houseType) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Update Property") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) { page ->
                    AsyncImage(
                        model = images[page],
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                EditableField("House Name", houseName) { houseName = it }
                EditableField("Rent", rent) { rent = it }
                EditableField("Location", location) { location = it }
                EditableField("Room Details", roomDetails) { roomDetails = it }
                EditableField("Facilities", facilities) { facilities = it }
                CategoryDropdown(houseType = houseType, onTypeChange = { houseType = it })
                EditableField("Description", description) { description = it }
                EditableField("Image Link 1", img1) { img1 = it }
                EditableField("Image Link 2", img2) { img2 = it }
                EditableField("Image Link 3", img3) { img3 = it }

                Button(
                    onClick = {
                        val updatedData = hashMapOf(
                            "houseName" to houseName,
                            "rent" to rent,
                            "location" to location,
                            "roomDetails" to roomDetails,
                            "houseType" to houseType,
                            "facilities" to facilities,
                            "description" to description,
                            "img1" to img1,
                            "img2" to img2,
                            "img3" to img3
                        )

                        firestore.collection("properties").document(propertyId)
                            .update(updatedData as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Property updated successfully!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error updating property: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Property")
                }
            }
        }
    }
}

@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = false,
        maxLines = 5
    )
}
