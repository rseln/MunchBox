package com.example.munchbox.ui.components

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
import com.example.munchbox.data.DataSource
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices
import com.example.munchbox.ui.OrderViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun OrderSummaries(order: OrderUiState, modifier: Modifier = Modifier){
    val orderService = StorageServices(FirebaseFirestore.getInstance()).orderService()

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
    /** Map of days in the week to a set of meals **/
    /** {MONDAY: {MEAL1, MEAL2}}**/
    val dayFilterMap = mutableMapOf<DayOfWeek,MutableSet<Meal>>()
    for(meal in order.meals){
        for(day in meal.days){
            dayFilterMap[day] = dayFilterMap.getOrDefault(day, mutableSetOf())
            dayFilterMap[day]!!.add(meal)
        }
    }
    /** Filter our in order list of days with the days with meals on the map**/
    val daysWithOrders = DayOfWeek.values().filter{
        dayFilterMap.contains(it)
    }
    for (day in daysWithOrders) {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = day.str,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier,
            )
            for(meal in dayFilterMap[day]!!){
                Spacer(modifier = Modifier.height(13.dp))
                OrderSummaryCard(meal = meal, orderService, true)
                Spacer(modifier = Modifier.height(13.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewOrderSummaries(){
    val viewModel: OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    viewModel.setMeals(meals = DataSource.allMeals.toList())
    viewModel.setPickupOptions(pickupOptions = DataSource.pickUpOptions)
    OrderSummaries(order = uiState)
}
