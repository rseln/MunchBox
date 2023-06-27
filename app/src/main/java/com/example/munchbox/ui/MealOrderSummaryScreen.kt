package com.example.munchbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource.allMeals
import com.example.munchbox.data.DataSource.currentDay
import com.example.munchbox.data.DataSource.pickUpOptions
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.components.OrderSummaryCard
import com.example.munchbox.ui.theme.Typography

@Composable
fun MealOrderSummaryScreen(
    orderUiState: OrderUiState,
    modifier: Modifier = Modifier,
    onNextButtonClicked: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
        .verticalScroll(scrollState),
    ){
        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.align( Alignment.CenterHorizontally)){
            Button(
                onClick= onNextButtonClicked,
            ) {
                Text("Order for next week")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        OrdersAvailable(orderUiState)
        OrderSummaries(orderUiState)
    }
}

@Composable
fun OrdersAvailable(order: OrderUiState){
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Orders Available for Pickup",
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            style = Typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
    }

    if (order.pickupOptions.isEmpty()){
        return
    }
    for(pickupDate in order.pickupOptions){
        if (currentDay == pickupDate){
            for(meal in order.meals){
                OrderSummaryCard(meal = meal)
            }
        }
    }


}

@Composable
fun OrderSummaries(order: OrderUiState){
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Order Summaries",
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            style = Typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
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


@Composable
@Preview
fun PreviewMealsAvailableScreen(){
    /** To show the cards go into interactive mode **/
    val viewModel:OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    viewModel.setMeals(meals = allMeals)
    viewModel.setPickupOptions(pickupOptions = pickUpOptions)
    MealOrderSummaryScreen(orderUiState = uiState)
}