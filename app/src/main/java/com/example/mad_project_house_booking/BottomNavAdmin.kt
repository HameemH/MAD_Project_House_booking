package com.example.mad_project_house_booking


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavAdmin(modifier: Modifier = Modifier,authViewModel: AuthViewModel,navController: NavHostController) {

    var currentRoute by remember { mutableStateOf("adminLanding") } // Initial route

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home,"adminLanding"),
        NavItem("Add", Icons.Default.Notifications,"addproperty"),
        NavItem("Bookings", Icons.Default.Notifications,"handleBooking")

        )



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentRoute == navItem.route,
                        onClick = {
                            currentRoute = navItem.route // Update route on click
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = "Icon")
                        },
                        label = {
                            Text(text = navItem.label)
                        }
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
            when (currentRoute) {
                "adminLanding" -> RoomSelectionScreenAdmin(navController ,authViewModel)
                "addproperty" -> AddPropertyForm(navController)
                "handleBooking" -> HandleBooking()

            }
        }
    }



}