package com.example.munchbox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.munchbox.R
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.OrderViewModel
import com.example.munchbox.ui.theme.Typography

@Composable
fun OrderSummaries(order: OrderUiState, modifier: Modifier = Modifier){
    Row{
        Box(
            modifier = Modifier
                .background(color = colorResource(R.color.button_purple))
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Order Summaries",
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                style = Typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.White,
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
            Text(
                text = day.str,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start=16.dp),
            )
            for(meal in dayFilterMap[day]!!){
                OrderSummaryCard(meal = meal)
            }
        }
    }
}

@Preview
@Composable
fun PreviewOrderSummaries(){
    val viewModel: OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    viewModel.setMeals(meals = DataSource.allMeals)
    viewModel.setPickupOptions(pickupOptions = DataSource.pickUpOptions)
    OrderSummaries(order = uiState)
}
