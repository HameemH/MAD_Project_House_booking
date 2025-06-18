package com.example.mad_project_house_booking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mad_project_house_booking.components.BottomNav
import com.example.mad_project_house_booking.components.BottomNavAdmin
import com.example.mad_project_house_booking.components.BottomNavGuest
import com.example.mad_project_house_booking.data_util.AuthViewModel
import com.example.mad_project_house_booking.screens.LoginAdmin
import com.example.mad_project_house_booking.screens.LoginPage
import com.example.mad_project_house_booking.screens.PropertyDetails
import com.example.mad_project_house_booking.screens.PropertyDetailsScreen
import com.example.mad_project_house_booking.screens.ScheduleBookForm
import com.example.mad_project_house_booking.screens.SimpleRegistrationForm
import com.example.mad_project_house_booking.screens.UpdateProperty


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authViewModel : AuthViewModel by viewModels()
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { LoginPage(navController,authViewModel) }
                        composable("registration") { SimpleRegistrationForm(navController,authViewModel) }
                        composable("userLanding"){ BottomNav(Modifier,authViewModel,navController) }
                        composable("adminLanding"){ BottomNavAdmin(Modifier,authViewModel,navController) }
                        composable("guestLanding"){ BottomNavGuest(Modifier,navController) }
                        composable("adminLogin"){ LoginAdmin(navController,authViewModel) }
                        composable("adminLogin"){ LoginAdmin(navController,authViewModel) }
                        composable("details/{propertyId}") { backStackEntry ->
                            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                            PropertyDetailsScreen(propertyId = propertyId, navController = navController)
                        }
                        composable("update/{propertyId}") { backStackEntry ->
                            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                            UpdateProperty(propertyId = propertyId, navController = navController)
                        }
                        composable("schedule/{propertyId}") { backStackEntry ->
                            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                            ScheduleBookForm(propertyId = propertyId, navController = navController)
                        }
                        composable("reqeustdetails/{propertyId}") { backStackEntry ->
                            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                         PropertyDetails(propertyId = propertyId, navController = navController)
                        }
                    }
                }
            }
        }
    }


}
