package com.example.mad_project_house_booking

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



@Composable
fun FavouriteScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val favourites = remember { mutableStateListOf<Room>() }
    val username by authViewModel.username

    LaunchedEffect(uid) {
        authViewModel.fetchUserProfile(uid)

        // Fetch favourite documents for this user
        firestore.collection("favorites")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val propertyIds = snapshot.documents.mapNotNull { it.getString("propertyId") }

                propertyIds.forEach { propertyId ->
                    firestore.collection("properties").document(propertyId).get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) {
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
                                    houseType = doc.getString("houseType") ?: "",
                                    isFavorited = false
                                )
                                favourites.add(room)
                            }
                        }
                }
            }
    }

    Column(modifier = Modifier.fillMaxWidth()) {


        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            LazyColumn {
                items(favourites) { room ->
                    FavouriteCard(
                        roomName = room.houseName,
                        price = room.rent,
                        isAvailable = room.isAvailable,
                        imageUrls = listOf(room.img1, room.img2, room.img3),
                        onBookClick = { navController.navigate("schedule/${room.id}") },
                        onDetailsClick = { navController.navigate("details/${room.id}")},
                        RemoveFav = {RemoveFavoriteProperty(context,uid=uid!!, room.id,onRemoved = {
                            favourites.remove(room) }
                        )}
                    )
                }
            }
        }
    }
}




@Composable
fun FavouriteCard(

    roomName: String,
    price: String,
    isAvailable: Boolean,
    imageUrls: List<String>,
    onBookClick: () -> Unit,
    onDetailsClick: () -> Unit,
    RemoveFav:   ()->Unit
)
{

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.DarkGray)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

            ImageSliderWithIndicators(imageUrls)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = roomName, fontSize = 20.sp, color = Color.Black, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Price: BDT $price / night", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAvailable) "Available" else "Not Available",
                fontSize = 14.sp,
                color = if (isAvailable) Color.Green else Color.Red
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onBookClick,
                    modifier = Modifier.weight(1f),
                    enabled = isAvailable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAvailable) Color.Blue else Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text("Book Schedule")
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = RemoveFav,
                    modifier = Modifier
                        .size(48.dp)
                        .border(1.5.dp, Color.Red, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Add to Favorites",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDetailsClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color.Blue)
            ) {
                Text("View Details", color = Color.Blue)
            }
        }
    }

}


fun RemoveFavoriteProperty(context: Context, uid: String, propertyId: String, onRemoved: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val favoritesRef = firestore.collection("favorites")

    favoritesRef
        .whereEqualTo("userId", uid)
        .whereEqualTo("propertyId", propertyId)
        .get()
        .addOnSuccessListener { snapshot ->
            for (document in snapshot.documents) {
                favoritesRef.document(document.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                        onRemoved()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to remove: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
}
