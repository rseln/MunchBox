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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource.currentDay
import com.example.munchbox.data.DataSource.fakeOrders
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.components.OrderSummaryCard
import com.example.munchbox.ui.theme.Typography

@Composable
fun MealOrderSummaryScreen(
    modifier: Modifier = Modifier,
    onCancelButtonClicked: () -> Unit = {},
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
        OrdersAvailable(fakeOrders)
        OrderSummaries(fakeOrders)
    }
}

@Composable
fun OrdersAvailable(orders:List<OrderUiState>){
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
    for (order in orders){
        if (order.pickupOptions.isEmpty()){
            continue
        }
        for(pickupDate in order.pickupOptions){
            if (currentDay == pickupDate){
                for(meal in order.meals){
                    OrderSummaryCard(meal = meal)
                }
            }
        }
    }

}

@Composable
fun OrderSummaries(orders: List<OrderUiState>){
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
    for(order in orders){
        for(meal in order.meals){
            for(day in meal.days){
                dayFilterMap[day] = dayFilterMap.getOrDefault(day, mutableSetOf())
                dayFilterMap[day]!!.add(meal)
            }
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
    MealOrderSummaryScreen()
}