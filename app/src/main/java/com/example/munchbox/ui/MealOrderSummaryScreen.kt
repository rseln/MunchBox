package com.example.munchbox.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource.allMeals
import com.example.munchbox.data.DataSource.currentDay
import com.example.munchbox.data.DataSource.pickUpOptions
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.components.OrderSummaries
import com.example.munchbox.ui.components.OrderSummaryCard
import com.example.munchbox.ui.components.SelectCard

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
        Row(modifier = Modifier.align( Alignment.CenterHorizontally)){
            SelectCard(headerText = "Let's munch.",
                buttonText = "Order Lunch",
                onClick = { onNextButtonClicked() })
        }
        Spacer(modifier = Modifier.height(32.dp))
        if (orderUiState.meals.filter { meal : Meal -> meal.days.contains(currentDay) }.isNotEmpty()){
            OrdersAvailable(orderUiState, Modifier)
        }
        if (orderUiState.meals.isNotEmpty()) {
            OrderSummaries(orderUiState, Modifier)
        }
    }
}

@Composable
fun OrdersAvailable(order: OrderUiState, modifier: Modifier = Modifier){
    Row{
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text(
                text = "Orders Available for Pickup",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }

    if (order.pickupOptions.isEmpty()){
        return
    }
    for(pickupDate in order.pickupOptions){
        if (currentDay == pickupDate){
            Column(modifier = modifier) {
                for(meal in order.meals){
                    OrderSummaryCard(meal = meal, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}


@Composable
@Preview
fun PreviewMealsAvailableScreen(){
    /** To show the cards go into interactive mode **/
    val viewModel:OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    viewModel.setMeals(meals = allMeals.toList())
    viewModel.setPickupOptions(pickupOptions = pickUpOptions)
    MealOrderSummaryScreen(orderUiState = uiState)
}