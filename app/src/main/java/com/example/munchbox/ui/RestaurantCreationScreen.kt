package com.example.munchbox.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantCreationScreen() {
    val restaurantName = remember { mutableStateOf(TextFieldValue()) }
    val restaurantAddress = remember { mutableStateOf(TextFieldValue()) }


    Column {
        Text(text = "Restaurant Name")
        TextField(value = restaurantName.value, onValueChange = {restaurantName.value = it})

        Spacer(modifier = Modifier.padding(20.dp))

        Text(text = "Address")
        TextField(value = restaurantAddress.value, onValueChange = {restaurantAddress.value = it})

        Spacer(modifier = Modifier.padding(20.dp))

        Text(text = "Restaurant Hours (Please enter in xx:xxAM - yy:yyPM form")

        Spacer(modifier = Modifier.padding(20.dp))

        Text(text = "Cuisine")

        Spacer(modifier = Modifier.padding(20.dp))

        Text(text = "Dietary Offerings")

        Spacer(modifier = Modifier.padding(20.dp))

        Text(text = "Business Phone Number")

        Spacer(modifier = Modifier.padding(20.dp))

        Text(text = "Business Email")

        Spacer(modifier = Modifier.padding(20.dp))

        Text(text = "Link Payment Account ")
        // TODO
        Spacer(modifier = Modifier.padding(20.dp))



    }
}

@Preview
@Composable
fun PreviewRestaurantCreationScreen() {
    RestaurantCreationScreen()
}