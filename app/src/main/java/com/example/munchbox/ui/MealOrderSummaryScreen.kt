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
import com.example.munchbox.data.OrderStorageService
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices
import com.example.munchbox.ui.components.OrderSummaries
import com.example.munchbox.ui.components.OrderSummaryCard
import com.example.munchbox.ui.components.SelectCard
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MealOrderSummaryScreen(
    orderUiState: OrderUiState,
    orderService: OrderStorageService,
    modifier: Modifier = Modifier,
    onConfirmButtonClicked: () -> Unit = {},
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
            OrdersAvailable(orderUiState, orderService ,onConfirmButtonClicked, Modifier)
        }
        if (orderUiState.meals.isNotEmpty()) {
            OrderSummaries(orderUiState, Modifier)
        }
    }
}

@Composable
fun OrdersAvailable(order: OrderUiState,
                    orderService: OrderStorageService,
                    onConfirmButtonClicked: () -> Unit = {},
                    modifier: Modifier = Modifier){
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

    //TODO: this is more technically correct but need to adjust when we do db integration
    Column(modifier = modifier) {
        // SHOULD JUST BE ONE VALUE SINCE WE CAN ONLY HAVE ONE MEAL PER DAY
        val mealsToday = order.meals.filter { meal : Meal -> meal.days.contains(currentDay) }
        for(meal in mealsToday) {
            OrderSummaryCard(meal = meal,
                orderService,
                false,
                onConfirmButtonClicked,
                modifier = Modifier.fillMaxWidth())
        }
    }
}


@Composable
@Preview
fun PreviewMealsAvailableScreen(){
    /** To show the cards go into interactive mode **/
    val viewModel: OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val orderService = StorageServices(FirebaseFirestore.getInstance()).orderService()

    viewModel.setMeals(meals = allMeals.toList())
    viewModel.setPickupOptions(pickupOptions = pickUpOptions)
    MealOrderSummaryScreen(orderUiState = uiState, orderService)
}