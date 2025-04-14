package com.example.mad_project_house_booking

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddPropertyForm(navController: NavHostController) {

    var location by remember { mutableStateOf("") }
    var houseName by remember { mutableStateOf("") }
    var houseType by remember { mutableStateOf("General") }
    var roomDetails by remember { mutableStateOf("") }
    var rent by remember { mutableStateOf("") }
    var facilities by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var img1 by remember { mutableStateOf("") }
    var img2 by remember { mutableStateOf("") }
    var img3 by remember { mutableStateOf("") }




    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Add Property",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5)
            )
        )

        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = houseName, onValueChange = { houseName = it }, label = { Text("House Name") }, modifier = Modifier.fillMaxWidth())
        CategoryDropdown(houseType = houseType, onTypeChange = { houseType = it })
        OutlinedTextField(value = roomDetails, onValueChange = { roomDetails = it }, label = { Text("Room Details") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rent, onValueChange = { rent = it }, label = { Text("Rent") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = facilities, onValueChange = { facilities = it }, label = { Text("Facilities") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = img1, onValueChange = { img1 = it }, label = { Text("Image Link 1") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = img2, onValueChange = { img2 = it }, label = { Text("Image Link 2") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = img3, onValueChange = { img3 = it }, label = { Text("Image Link 3") }, modifier = Modifier.fillMaxWidth())


        Button(
            onClick = {


                val property = hashMapOf(
                    "location" to location,
                    "houseName" to houseName,
                    "houseType" to houseType,
                    "roomDetails" to roomDetails,
                    "rent" to rent,
                    "facilities" to facilities,
                    "description" to description,
                    "isAvailable" to true,
                    "img1" to getDirectDriveLink(img1),
                    "img2" to getDirectDriveLink(img2),
                    "img3" to getDirectDriveLink(img3)
                )

                FirebaseFirestore.getInstance()
                    .collection("properties")
                    .add(property)
                    .addOnSuccessListener {
                        Toast.makeText(context, "✅ Property uploaded!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // Navigate back if needed
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "❌ Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Add Property")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(houseType: String, onTypeChange: (String) -> Unit) {
    val categories = listOf("General", "Luxury", "Premium")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = houseType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onTypeChange(category)
                        expanded = false
                    }
                )
            }
        }
    }
}


fun getDirectDriveLink(driveLink: String): String {
    val regex = Regex("""/d/([a-zA-Z0-9_-]+)""")
    val match = regex.find(driveLink)
    val fileId = match?.groupValues?.get(1)
    return if (fileId != null) {
        "https://drive.google.com/uc?export=download&id=$fileId"
    } else {
        driveLink // fallback to original
    }
}
