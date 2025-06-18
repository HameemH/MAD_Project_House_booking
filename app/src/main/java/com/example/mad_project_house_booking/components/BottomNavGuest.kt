package com.example.mad_project_house_booking.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mad_project_house_booking.data_util.NavItem
import com.example.mad_project_house_booking.R
import com.example.mad_project_house_booking.screens.Rules
import com.example.mad_project_house_booking.screens.RoomSelectionScreenGuest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavGuest(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var currentRoute by remember { mutableStateOf("guestLanding") }

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home, "guestLanding"),
        NavItem("Rules", Icons.Default.Notifications, "rules")
    )


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                tonalElevation = 6.dp,
                containerColor = Color.White.copy(alpha = 0.98f),
                modifier = Modifier.height(64.dp)
            ) {
                navItemList.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentRoute == navItem.route,
                        onClick = { currentRoute = navItem.route },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = navItem.label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Stylish logo + title (same as user and admin version)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.premier_logo),
                    contentDescription = "PremierHouse Logo",
                    modifier = Modifier.size(34.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PremierHouse",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
                        ),

                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Guest Pages
            when (currentRoute) {
                "guestLanding" -> RoomSelectionScreenGuest(navController)
                "rules" -> Rules(navController)  // Pass the navController here
            }

        }
        }
    }

