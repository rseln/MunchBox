package com.example.munchbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.example.munchbox.data.DataSource.allMeals
import com.example.munchbox.data.DataSource.currentDay
import com.example.munchbox.data.DataSource.pickUpOptions
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.components.OrderSummaries
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
        modifier = Modifier
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
        OrdersAvailable(orderUiState, modifier)
        OrderSummaries(orderUiState, modifier)
    }
}

@Composable
fun OrdersAvailable(order: OrderUiState, modifier: Modifier = Modifier){
    Row{
        Box(
            modifier = Modifier
                .background(color = colorResource(R.color.button_purple))
                .padding(16.dp)
                .fillMaxWidth()
        ){
            Text(
                text = "Orders Available for Pickup",
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                color = Color.White,
                style = Typography.headlineSmall,
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
                    OrderSummaryCard(meal = meal)
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
    viewModel.setMeals(meals = allMeals)
    viewModel.setPickupOptions(pickupOptions = pickUpOptions)
    MealOrderSummaryScreen(orderUiState = uiState)
}