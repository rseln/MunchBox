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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource.currentDay
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.components.OrderSummaryCard
import com.example.munchbox.ui.components.RestaurantAddMealCard
import com.example.munchbox.ui.components.SelectCard

@Composable
fun RestaurantHubScreen(
    orderUiState: OrderUiState,
    modifier: Modifier = Modifier,
) {
    var expanded = remember { mutableStateOf(false) }
    var selectedOptions = remember { mutableStateOf(setOf<DietaryOption>()) }
    var selectedDays = remember { mutableStateOf(setOf<DayOfWeek>()) }

        val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollState),
    ){
        Row(modifier = Modifier.align( Alignment.CenterHorizontally)){
            if (!expanded.value) {
                SelectCard(headerText = "Add a new meal.",
                    buttonText = "Create",
                    onClick = { expanded.value = true })
            }
            else {
                RestaurantAddMealCard(
                    selectedOptions = selectedOptions.value,
                    selectedDays = selectedDays.value,
                    added = false,
                    onSelectOptionCallback = {option : DietaryOption ->
                        if (selectedOptions.value.contains(option)) {
                            selectedOptions.value = selectedOptions.value.minus(option)
                        }
                        else {
                            selectedOptions.value = selectedOptions.value.plus(option)
                        }
                    },
                    onSelectDayCallback = { day : DayOfWeek ->
                        if (selectedDays.value.contains(day)) {
                            selectedDays.value = selectedDays.value.minus(day)
                        }
                        else {
                            selectedDays.value = selectedDays.value.plus(day)
                        }
                    },
                    onAddCallback = { newDietaryOptions : Set<DietaryOption>, newDays : Set<DayOfWeek> ->
                        expanded.value = false
                    },
                    onCancelCallback = { expanded.value = false },
                    modifier = Modifier
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        if (orderUiState.meals.filter { meal : Meal -> meal.days.contains(currentDay) }.isNotEmpty()){
            MealsAvailable(orderUiState, Modifier)
        }
    }
}

@Composable
fun MealsAvailable(order: OrderUiState, modifier: Modifier = Modifier){
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
