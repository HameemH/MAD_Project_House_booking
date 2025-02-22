package com.example.mad_project_house_booking

import RoomSelectionScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mad_project_house_booking.ui.theme.MAD_Project_House_bookingTheme

import com.example.mad_project_house_booking.SimpleRegistrationForm // Import Registration.kt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { LoginPage(navController) }
                        composable("registration") { SimpleRegistrationForm(navController) }
                        composable("userLanding"){ RoomSelectionScreen()
                        }
                        composable("adminLanding"){}
                        composable("addproperty"){ AddPropertyForm() }
                    }
                }
            }
        }
    }
}
