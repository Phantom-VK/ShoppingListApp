package com.example.shoppinglistapp

import android.graphics.drawable.Icon
import android.text.Layout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ShoppingItem(val id: Int, var name: String, var quantity: Int, var isEditing: Boolean = false)
@Composable
fun ShoppingListApp(){
    var sItems by remember{ mutableStateOf(listOf<ShoppingItem>( )) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {

            Text(text = "Add Item")
        }
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)){
            items(sItems){
                ShoppingListItem(item = it, onEditClick = { /*TODO*/ }) {
                    
                }
            }
        }
    }
    if (showDialog){
        
        AlertDialog(onDismissRequest = { showDialog = false },
            confirmButton = {
                            Row (modifier = Modifier
                                .fillMaxWidth()
                                .padding(7.dp),
                                horizontalArrangement = Arrangement.Absolute.SpaceBetween){

                                Button(onClick = { showDialog = false }) {
                                    Text(text = "Cancel")
                                }
                                Button(onClick = {
                                    if(itemName.isNotBlank()){
                                        val newItem = ShoppingItem(
                                            id = sItems.size + 1,
                                            quantity = itemQuantity.toInt(),
                                            name = itemName)
                                        sItems += newItem
                                        showDialog = false
                                        itemName = ""
                                        itemQuantity = ""
                                    }
                                }) {
                                    Text(text = "Add")
                                }

                            }
            },
            containerColor = AlertDialogDefaults.containerColor,
            title = {Text(text = "Add Shopping Item", color = Color.Yellow )},
            text = {
                Column {
                    OutlinedTextField(value = itemName, onValueChange = {
                        itemName = it
                    },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(7.dp))
                    Spacer(modifier = Modifier.padding(16.dp))
                    OutlinedTextField(value = itemQuantity, onValueChange = {
                        itemQuantity = it
                    },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(7.dp))
                }
            })
    }
}


@Composable
fun ShoppingListItem(item:ShoppingItem, onEditClick:()->Unit, onDeleteClick:()->Unit){

    Card(modifier = Modifier
        .fillMaxWidth()
        ,
        shape = RoundedCornerShape(20)
        ,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Row (modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()){
            Column {
                Text(text = item.name, modifier = Modifier.padding(10.dp), color = Color.White)
                Text(
                    text = "Qty: ${item.quantity}",
                    modifier = Modifier.padding(10.dp),
                    color = Color.White
                )
            }
        Spacer(modifier = Modifier.padding(end = 150.dp))
        IconButton(modifier = Modifier.padding(top = 20.dp), onClick = onEditClick) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit item ${item.name}")
        }
        IconButton(modifier = Modifier.padding(top = 20.dp), onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete item ${item.name}"
            )

        }

    }

    }
    Spacer(modifier = Modifier.padding(vertical = 8.dp))

}