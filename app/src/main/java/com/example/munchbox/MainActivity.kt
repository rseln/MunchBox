package com.example.munchbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MunchBoxApp()
//            MealCardContainer()
//            NumberOfMealsScreen()
//            OrderSummaryCard(OrderUiState(restaurant = Restaurant("Lazeez", setOf())))
//            MealOrderSummaryScreen()
        }
    }
}
