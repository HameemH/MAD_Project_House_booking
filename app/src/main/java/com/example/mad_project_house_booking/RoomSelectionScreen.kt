import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mad_project_house_booking.AuthViewModel
import com.example.mad_project_house_booking.BottomNav
import com.example.mad_project_house_booking.R
import com.example.mad_project_house_booking.Room
import com.example.mad_project_house_booking.RoomSelectionCard

@Composable
fun RoomSelectionScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    // List of rooms
    val rooms = remember {
        mutableStateListOf(
            Room("Deluxe Suite", "5000", true, R.drawable.room1),
            Room("Sonar Bangla Room", "3500", false, R.drawable.room2),
            Room("Meghna Retreat", "7500", true, R.drawable.room3),
            Room("Chhayabithi Room", "4000", true, R.drawable.room4),
            Room("Comfort Stay", "2500", true, R.drawable.room5),
            Room("Velvet Luxe", "9000", true, R.drawable.room6),
            Room("Sundori Suite", "8000", true, R.drawable.room7)
        )
    }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    val categories = listOf("All Categories", "Suite", "Standard", "Luxury")

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
                    text = "User", // Replace with actual user name
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
            items(rooms) { room ->
                RoomSelectionCard(
                    roomName = room.name,
                    price = room.price,
                    isAvailable = room.isAvailable,
                    imageResId = room.imageResId
                ) {
                    println("${room.name} booked!")
                }
            }
        }

       // BottomNav()
    }




}}