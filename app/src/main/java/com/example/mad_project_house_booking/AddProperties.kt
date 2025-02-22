package com.example.mad_project_house_booking


// AddProperties.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun AddPropertyForm() {

    var location by remember { mutableStateOf("") }
    var houseName by remember { mutableStateOf("") }
    var houseType by remember { mutableStateOf("") }
    var roomDetails by remember { mutableStateOf("") }
    var rent by remember { mutableStateOf("") }
    var facilities by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    //var images by remember { mutableStateListOf<Uri>() }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            uris.forEach { uri ->
               // images.add(uri)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Add Property", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5)))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = houseName,
            onValueChange = { houseName = it },
            label = { Text("House Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = houseType,
            onValueChange = { houseType = it },
            label = { Text("House Type") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = roomDetails,
            onValueChange = { roomDetails = it },
            label = { Text("Room Details") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rent,
            onValueChange = { rent = it },
            label = { Text("Rent") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = facilities,
            onValueChange = { facilities = it },
            label = { Text("Facilities") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3), // Replace backgroundColor
                    contentColor = Color.White // contentColor remains the same
                )
            ) {
                //Icon(Icons.Filled.Image, contentDescription = "Add Image")
                Text("Add Images")
            }
        }

//        if (images.isNotEmpty()) {
//            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
//                images.forEach { uri ->
//                    AsyncImage(
//                        model = uri,
//                        contentDescription = "Selected Image",
//                        modifier = Modifier
//                            .size(100.dp)
//                            .padding(4.dp)
//                            .clip(RoundedCornerShape(8.dp)),
//                        contentScale = ContentScale.Crop
//                    )
//                }
//            }
//        }

        Button(
            onClick = {
                val propertyData = mapOf(
                    "location" to location,
                    "houseName" to houseName,
                    "houseType" to houseType,
                    "roomDetails" to roomDetails,
                    "rent" to rent,
                    "facilities" to facilities,
                    "description" to description,
//                    "images" to images.map {
//                        var it = null
//                        it.toString()
//                    } // Store image URIs as strings
                )
               // onPropertyAdded(propertyData)
                Toast.makeText(context, "Property data ready to send firebase", Toast.LENGTH_SHORT).show()
                // Here you would add the Firebase logic to push the data
                // For example: FirebaseDatabase.getInstance().reference.child("properties").push().setValue(propertyData)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
           // colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50), contentColor = Color.White)
        ) {
            Text("Submit Property")
        }
    }
}