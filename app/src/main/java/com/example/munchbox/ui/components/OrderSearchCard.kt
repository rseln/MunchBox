package com.example.munchbox.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSearchCard(
    storageServices: StorageServices,
    restaurant: Restaurant,
    orderUiState: OrderUiState)
{
    var search by remember { mutableStateOf(TextFieldValue("")) }
    // -1 is unverified, 0 is false, 1 is true
    var isOrderIdValid by remember { mutableStateOf(-1) }
    var isButtonDisabled by remember { mutableStateOf(true) }
    var isOrderExist by remember { mutableStateOf<Boolean>(false) }

    val coroutineScope = rememberCoroutineScope()
    val fmt = SimpleDateFormat("yyyyMMdd")
    // only display if there are meals to fulfill today from the restaurant
    //if (orderUiState.orders.any { order: Order -> fmt.format(order.pickUpDate).equals(fmt.format(Date())) && order.restaurantID == restaurant.restaurantID}) {
        ElevatedCard(
            modifier = Modifier.height(IntrinsicSize.Max),
            shape = MaterialTheme.shapes.large
        )//FCA9
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
                            isButtonDisabled = search.text == "" || search.text.length != 4
                            isOrderIdValid = -1 // idk to keep or not to keep for ui sake
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    if (!isButtonDisabled) {
                        ElevatedButton(
                            onClick = {
                              coroutineScope.launch {
                                  isOrderExist = storageServices.orderService().checkRestaurantOrderExists(search.text, restaurant.restaurantID)
                                  isOrderIdValid = if(isOrderExist) 1 else 0
                              }
                            },
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
    //}

}

@Preview
@Composable
fun OrderSearchPreview(){
    val lazeezMeal = setOf(Meal("temp_meal_id", "temp_restaurant_id", setOf(DietaryOption.VEGETARIAN, DietaryOption.GLUTEN_FREE), setOf(DayOfWeek.TUESDAY), mapOf(Pair(DayOfWeek.TUESDAY, 20))))
//    OrderSearchCard(DataSource.lazeez, lazeezMeal)
}