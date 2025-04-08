package com.example.mad_project_house_booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,ExperimentalPagerApi::class)

@Composable
fun PropertyDetailsScreen(propertyId: String, navController: NavHostController) {
    val firestore = FirebaseFirestore.getInstance()
    var property by remember { mutableStateOf<Room?>(null) }

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
                    description = doc.getString("description") ?: ""
                )
            }
    }

    property?.let { room ->
        val images = listOf(room.img1, room.img2, room.img3)
        val pagerState = rememberPagerState(pageCount = { images.size })

        // 🔁 Auto-scroll
        LaunchedEffect(pagerState) {
            while (true) {
                delay(3000)
                val next = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(next)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Property Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
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

                Spacer(modifier = Modifier.height(8.dp))

               // ImageSliderWithIndicators(images)

                Spacer(modifier = Modifier.height(16.dp))

                NonEditableField("House Name", room.houseName)
                NonEditableField("Rent", room.rent)
                NonEditableField("Location", room.location)
                NonEditableField("Room Details", room.roomDetails)
                NonEditableField("Facilities", room.facilities)
                NonEditableField("Description", room.description)
            }
        }
    }
}



@Composable
fun NonEditableField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        readOnly = true,
        singleLine = false,
        maxLines = 5
    )
}
