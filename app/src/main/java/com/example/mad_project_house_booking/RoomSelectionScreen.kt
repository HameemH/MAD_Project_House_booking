package com.example.mad_project_house_booking
import android.content.Context
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RoomSelectionScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    // List of rooms
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

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val username by authViewModel.username

    val context = LocalContext.current

    LaunchedEffect(uid) {
        uid?.let { authViewModel.fetchUserProfile(it) }
    }

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
                    text = "Welcome, $username", // Personalized greeting
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
                    authViewModel.signout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text("Sign Out", color = Color.Red)
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
                RoomSelectionCard(
                    roomName = room.houseName,
                    price = room.rent,
                    isAvailable = room.isAvailable,
                    imageUrls = listOf(room.img1, room.img2, room.img3),
                    onBookClick = { navController.navigate("schedule/${room.id}") },
                    onDetailsClick = { navController.navigate("details/${room.id}") },
                    addFav = { ToggleFavoriteProperty(context, uid = uid!!, propertyId = room.id) },
                    isFavorited = room.isFavorited
                )
            }
        }
    }
}

fun ToggleFavoriteProperty(context: Context, uid: String, propertyId: String) {
    val firestore = FirebaseFirestore.getInstance()
    val favoritesRef = firestore.collection("favorites")

    val query = favoritesRef
        .whereEqualTo("userId", uid)
        .whereEqualTo("propertyId", propertyId)

    query.get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                val favData = hashMapOf(
                    "userId" to uid,
                    "propertyId" to propertyId,
                    "timestamp" to System.currentTimeMillis()
                )

                favoritesRef.add(favData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(context, "Already in favorites!", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
}
