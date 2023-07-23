package com.example.munchbox.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Order
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.DataSource
import com.example.munchbox.data.StorageServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSearchCard(
    storageServices: StorageServices,
    restaurant: Restaurant,
    meals: Set<Meal>)
{
    var search by remember { mutableStateOf(TextFieldValue("")) }
    // -1 is unverified, 0 is false, 1 is true
    var isOrderIdValid by remember { mutableStateOf(-1) }
    var isButtonDisabled by remember { mutableStateOf(true) }
    var isVerifyClicked by remember {mutableStateOf<Boolean>(false)}

    var isOrderExist by remember { mutableStateOf<Boolean>(false) }
    LaunchedEffect(isVerifyClicked){
        isOrderExist = storageServices.orderService().checkOrderExistsByOrderID(search.text)
        isOrderIdValid = if(isOrderExist) 1 else 0
        isVerifyClicked = false
    }
    val today = DataSource.currentDay

    // only display if there are meals to fulfill today from the restaurant
    if (meals.any { meal: Meal -> meal.days.contains(DataSource.currentDay) && meal.restaurantID == restaurant.restaurantID}) {
        ElevatedCard(
            modifier = Modifier.height(IntrinsicSize.Max),
            shape = MaterialTheme.shapes.large
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Verify Order ID",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "Check if the order number is valid.",
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.weight(1f, false)) {
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .width(240.dp),
                        label = { Text(text = "Order Id") },
                        value = search,
                        onValueChange = {
                            search = it
                            isButtonDisabled = search.text == ""
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    if (!isButtonDisabled) {
                        ElevatedButton(
                            onClick = { isVerifyClicked = true }, //runs the launched effect
                        ) {
                            Text(
                                text = "Verify",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                if (isOrderIdValid == 0) {
                    Text(
                        text = "Order ID does not exist.",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Red
                    )
                }
                if (isOrderIdValid == 1) {
                    Text(
                        text = "Order ID is valid!",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(34, 139, 34)
                    )
                }
            }
        }
    }
}

//TODO: EDIT THIS FUNCTION DURING INTEGRATION TO MATCH THE ACTUAL DATA STRUCTURE
//private fun checkOrderExists(id: TextFieldValue, meals: Set<Meal>, today : DayOfWeek, restaurant: Restaurant): Int {
//    // Edit this logic later
//    if (meals.any { meal: Meal -> meal.days.contains(today) && meal.restaurantID == restaurant.restaurantID}) {
//        return 1
//    }
//    return 0
//}

@Preview
@Composable
fun OrderSearchPreview(){
    val lazeezMeal = setOf(Meal("temp_meal_id", "temp_restaurant_id", setOf(DietaryOption.VEGETARIAN, DietaryOption.GLUTEN_FREE), setOf(DayOfWeek.TUESDAY), mapOf(Pair(DayOfWeek.TUESDAY, 20))))
//    OrderSearchCard(DataSource.lazeez, lazeezMeal)
}