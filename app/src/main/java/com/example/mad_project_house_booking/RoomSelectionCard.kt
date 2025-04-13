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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale


@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun RoomSelectionCard(
    roomName: String,
    price: String,
    isFavorited: Boolean,
    isAvailable: Boolean,
    imageUrls: List<String>,
    onBookClick: () -> Unit,
    onDetailsClick: () -> Unit,
    addFav:   ()->Unit,
    revealComments: ()-> Unit
) {

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
            Text(text = "Price: BDT $price / Month", fontSize = 16.sp, color = Color.Gray)
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
                    onClick = addFav,
                    modifier = Modifier
                        .size(48.dp)
                        .border(1.5.dp, Color.Blue, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
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
            OutlinedButton(
                onClick = revealComments,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Text("View Comment", color = Color.Red)
            }
        }
    }
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageSliderWithIndicators(imageUrls: List<String>) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            AsyncImage(
                model = imageUrls[page],
                contentDescription = "Property image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row {
                repeat(imageUrls.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.Blue else Color.LightGray)
                    )
                }
            }
        }
    }
}

