package com.example.mad_project_house_booking

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RoomSelectionScreenAdmin(navController: NavHostController, authViewModel: AuthViewModel) {
    // List of rooms

    val rooms = remember { mutableStateListOf<Room>() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRoomId by remember { mutableStateOf("") }
    val context = LocalContext.current

    var showCategoryDropdown by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    val categories = listOf("All Categories", "General", "Luxury", "Premium")

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("properties").get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { doc ->
                    val room = Room(
                        id = doc.id,
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
                    rooms.add(room)
                }
            }
    }
    val filteredRooms = remember(rooms, selectedCategory) {
        if (selectedCategory == "All Categories") rooms
        else rooms.filter { it.houseType == selectedCategory }
    }


    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val username by authViewModel.username

    LaunchedEffect(uid) {
        uid?.let { authViewModel.fetchUserProfile(it) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth().background(Color.LightGray)
                .padding(0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // User Info and Sign Out
            Column {
                Text(
                    text = username, // Replace with actual user name
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(
                    onClick = {
                        authViewModel.signout()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text("Sign Out", color = Color.Red)
                }
            }

            // Category Dropdown
            Box {
                OutlinedButton(
                    onClick = { showCategoryDropdown = true },
                    modifier = Modifier.width(180.dp)
                ) {
                    Text(
                        text = selectedCategory,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Categories",
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false },
                    modifier = Modifier.width(180.dp)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            onClick = {
                                selectedCategory = category
                                showCategoryDropdown = false
                            }
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {


            // LazyColumn for displaying multiple rooms
            LazyColumn {
                items(filteredRooms) { room ->
                    RoomSelectionCardAdmin (
                        roomName = room.houseName,
                        price = room.rent,
                        isAvailable = room.isAvailable,
                        imageUrls = listOf(room.img1, room.img2, room.img3),
                        onUpdateClick = { navController.navigate("update/${room.id}") },
                        onDetailsClick = { selectedRoomId = room.id
                            showDeleteDialog = true }
                    )
                }
            }

            if (showDeleteDialog && selectedRoomId != null) {
                DeletePropertyDialog(
                    roomId = selectedRoomId!!,
                    onDeleteConfirmed = {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "Property deleted.", Toast.LENGTH_SHORT).show()
                        }
                        rooms.removeIf { it.id == selectedRoomId }
                        showDeleteDialog = false
                        selectedRoomId = ""
                        // Optionally: remove the room from list
                    },
                    onDismiss = {
                        showDeleteDialog = false
                        selectedRoomId = ""
                    }
                )
            }



        }


    }}


@Composable
fun DeletePropertyDialog(
    roomId: String,
    onDeleteConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Property") },
        text = { Text("Are you sure you want to delete this property?") },
        confirmButton = {
            TextButton(onClick = {
                firestore.collection("properties").document(roomId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Property deleted.", Toast.LENGTH_SHORT).show()
                        onDeleteConfirmed()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to delete: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
