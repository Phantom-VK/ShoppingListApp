package com.example.shoppinglistapp

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController

// Data class representing a shopping item
data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var address: String,
    var isEditing: Boolean = false
)

// Composable function to edit a shopping item
@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // Text field to edit item name
                BasicTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    singleLine = true,
                    modifier = Modifier
                        .padding(7.dp)
                        .wrapContentWidth()
                        .padding(start = 20.dp),
                    textStyle = TextStyle(color = Color.White)
                )
                // Text field to edit item quantity
                BasicTextField(
                    value = editedQuantity,
                    onValueChange = { editedQuantity = it },
                    singleLine = true,
                    modifier = Modifier
                        .padding(7.dp)
                        .wrapContentWidth()
                        .padding(start = 20.dp),
                    textStyle = TextStyle(color = Color.White)
                )
            }
            // Button to save the edited item
            Button(
                onClick = {
                    onEditComplete(editedName, editedQuantity.toInt())
                },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(text = "Save")
            }
        }
    }
}

// Main composable function for the shopping list app
@Composable
fun ShoppingListApp(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    navController: NavController,
    context: Context,
    address: String
) {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        // Button to show the dialog for adding a new item
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add Item")
        }
        // LazyColumn to display the list of shopping items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sItems) { item ->
                if (item.isEditing) {
                    // Show editor for the item being edited
                    ShoppingItemEditor(item = item, onEditComplete = { editedName, editedQuantity ->
                        // Update the list with edited item and close editor
                        sItems = sItems.map { it.copy(isEditing = false) }
                        val editedItemIndex = sItems.indexOfFirst { it.id == item.id }
                        if (editedItemIndex != -1) {
                            sItems = sItems.toMutableList().apply {
                                this[editedItemIndex] = this[editedItemIndex].copy(
                                    name = editedName,
                                    quantity = editedQuantity,
                                    isEditing = false,
                                    address = address

                                )
                            }
                        }
                    })
                } else {
                    // Show item in the list
                    ShoppingListItem(item = item, onEditClick = {
                        // Set the item to editing mode
                        sItems = sItems.map { it.copy(isEditing = it.id == item.id) }
                    }, onDeleteClick = {
                        // Remove the item from the list
                        sItems = sItems.filter { it.id != item.id }
                    })
                }
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                //Have location access
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                // ASK FOR ACCESS
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (rationalRequired) {
                    Toast.makeText(
                        context, "Location access is required for this feature to work!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Location access is required for this feature to work! Please enable it in mobile settings",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

            }

        })

    // Dialog to add a new item
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Cancel button
                    Button(onClick = { showDialog = false }) {
                        Text(text = "Cancel")
                    }
                    // Add button
                    Button(onClick = {
                        if (itemName.isNotBlank() && itemQuantity.isNotBlank()) {
                            val newItem = ShoppingItem(
                                id = sItems.size + 1,
                                name = itemName,
                                quantity = itemQuantity.toInt(),
                                address
                            )
                            sItems = sItems + newItem
                            showDialog = false
                            itemName = ""
                            itemQuantity = ""
                        }
                    }) {
                        Text(text = "Add")
                    }

                    Button(onClick = {
                        if(locationUtils.hasLocationPermission(context)){
                            locationUtils.requestLocationUpdates(viewModel)
                            navController.navigate("locationscreen"){
                                this.launchSingleTop
                            }
                        }else{
                                requestPermissionLauncher.launch(arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ))
                        }
                    }) {
                        Text(text = "Address")

                    }
                }
            },
            containerColor = AlertDialogDefaults.containerColor,
            title = { Text(text = "Add Shopping Item", color = Color.Yellow) },
            text = {
                Column {
                    // Input field for item name
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(7.dp)
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    // Input field for item quantity
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(7.dp)
                    )
                }
            }
        )
    }
}

// Composable function to display a shopping item
@Composable
fun ShoppingListItem(item: ShoppingItem, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                // Display item name
                Text(text = item.name, modifier = Modifier.padding(10.dp), color = Color.White)
                // Display item quantity
                Text(
                    text = "Qty: ${item.quantity}",
                    modifier = Modifier.padding(10.dp),
                    color = Color.White
                )

                Text(text = "Address: ${item.address}" )
            }
            Row (horizontalArrangement = Arrangement.SpaceEvenly){
                // Button to edit the item
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit item ${item.name}"
                    )
                }
                // Button to delete the item
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete item ${item.name}"
                    )
                }
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Add item Location",
                )
            }
        }
    }
    Spacer(modifier = Modifier.padding(vertical = 8.dp))
}
