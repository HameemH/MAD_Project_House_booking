package com.example.mad_project_house_booking

import RoomSelectionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mad_project_house_booking.ui.theme.MAD_Project_House_bookingTheme


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
                        composable("adminLogin"){ LoginAdmin(navController,authViewModel) }
                        composable("details/{propertyId}") { backStackEntry ->
                            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
                            PropertyDetailsScreen(propertyId = propertyId, navController = navController)
                        }
                    }
                }
            }
        }
    }


}
