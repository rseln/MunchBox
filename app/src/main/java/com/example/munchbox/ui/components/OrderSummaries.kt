package com.example.munchbox.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Order
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices
import com.example.munchbox.ui.OrderViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun OrderSummaries(
    orderUiState: OrderUiState,
    storageServices: StorageServices,
    modifier: Modifier = Modifier,
){
    Row (
        modifier = modifier
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "Order Summaries",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
    //skull emoji moment
    val dayToOrderMap: MutableMap<DayOfWeek, MutableList<Order>> = mutableMapOf()
    for(day in DayOfWeek.values()){
        dayToOrderMap[day] = mutableListOf()
    }
    for((order,pickUpDate) in orderUiState.orderToPickupDay) {
        dayToOrderMap[pickUpDate]!!.add(order)
    }
    val dayToOrderList = dayToOrderMap.toList().sortedBy { it.first.id }
    for((day, orders) in dayToOrderList){
        if (orders.isNotEmpty()){
            Column(modifier = modifier) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = day.str,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier,
                )
                for(order in orders){
                    val meal = orderUiState.orderToMeal[order]
                    if(meal!=null){
                        Spacer(modifier = Modifier.height(13.dp))
                        Log.d("HELLO FROM ORDER SUMMARY3", meal.mealID)
                        OrderSummaryCard(meal = meal, storageServices = storageServices, confirmDisabled = true)
                        Spacer(modifier = Modifier.height(13.dp))
                    }
                }

            }
        }

    }
}
@Preview
@Composable
fun PreviewOrderSummaries(){
    val viewModel: OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val storageService = StorageServices(FirebaseFirestore.getInstance())
//    viewModel.setMeals(meals = DataSource.allMeals.toList())
//    viewModel.setPickupOptions(pickupOptions = DataSource.pickUpOptions)
    OrderSummaries(orderUiState = uiState, storageService)
}
