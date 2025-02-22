import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mad_project_house_booking.BottomNav
import com.example.mad_project_house_booking.R
import com.example.mad_project_house_booking.Room
import com.example.mad_project_house_booking.RoomSelectionCard

@Composable
fun RoomSelectionScreen() {
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Choose a Room", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(10.dp))

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




}