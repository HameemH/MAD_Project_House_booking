package com.example.mad_project_house_booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@Composable
fun RoomSelectionScreenGuest(navController: NavHostController)
{
    val rooms = remember { mutableStateListOf<Room>() }

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("properties").get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { doc ->
                    val room = Room(
                        id = doc.id,
                        houseName = doc.getString("houseName") ?: "",
                        rent = doc.getString("rent") ?: "",
                        isAvailable = doc.getBoolean("isAvailable") ?: true,
                        img1 = doc.getString("img1") ?: "",
                        img2 = doc.getString("img2") ?: "",
                        img3 = doc.getString("img3") ?: "",
                        location = doc.getString("location") ?: "",
                        roomDetails = doc.getString("roomDetails") ?: "",
                        facilities = doc.getString("facilities") ?: "",
                        description = doc.getString("description") ?: "",
                        houseType = doc.getString("houseType") ?: "",
                        isFavorited = false // Set initial state of isFavorited to false
                    )
                    rooms.add(room)

                }
            }
    }

    var showCategoryDropdown by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    val categories = listOf("All Categories", "General", "Luxury", "Premium")

    val filteredRooms = remember(rooms, selectedCategory) {
        if (selectedCategory == "All Categories") rooms
        else rooms.filter { it.houseType == selectedCategory }
    }





    var showCommentsDialog by remember { mutableStateOf(false) }
    var currentPropertyId by remember { mutableStateOf("") }
    var currentPropertyName by remember { mutableStateOf("") }



    Column(modifier = Modifier.fillMaxWidth()) {
        // Header Row with Gradient Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1E3C72), Color(0xFF2A5298)) // Gradient from dark blue to lighter blue
                    )
                )
                .padding(vertical = 16.dp, horizontal = 24.dp) // Padding for header spacing
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Welcome, Guest", // Personalized greeting
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White, fontWeight = FontWeight.Bold
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Find your perfect home",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.7f))
                )
            }

            // Sign-out button at the top right corner
            TextButton(
                onClick = {

                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text("Login", color = Color.Red)
            }
        }

        // Category Dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedButton(
                onClick = { showCategoryDropdown = true },
                modifier = Modifier.fillMaxWidth()
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

    // Main Content
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn {
            items(filteredRooms) { room ->
                RoomSelectionCardGuest(
                    roomName = room.houseName,
                    price = room.rent,
                    isAvailable = room.isAvailable,
                    imageUrls = listOf(room.img1, room.img2, room.img3),
                    onpopUpClick = {navController.navigate("login")  },
                    onDetailsClick = { navController.navigate("details/${room.id}") },
                    revealComments={ currentPropertyId = room.id
                        currentPropertyName = room.houseName
                        showCommentsDialog = true}
                )
            }
        }
    }

    if (showCommentsDialog) {
        PropertyCommentsDialog(
            propertyId = currentPropertyId,
            propertyName = currentPropertyName,
            onDismiss = { showCommentsDialog = false }
        )
    }
}

