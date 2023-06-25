package com.example.munchbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.components.OrderSummaryCard


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MunchBoxApp()
//            MealCardContainer()
//            NumberOfMealsScreen()
//            OrderSummaryCard(OrderUiState(restaurant = Restaurant("Lazeez", setOf())))
        }
    }
}
