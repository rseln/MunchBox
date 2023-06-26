package com.example.munchbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.munchbox.ui.components.MealCardContainer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.MealOrderSummaryScreen
import com.example.munchbox.ui.NumberOfMealsScreen
import com.example.munchbox.ui.components.OrderSummaryCard
import com.example.munchbox.ui.theme.GreetingCardTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            MealCardContainer()
//            NumberOfMealsScreen()
//            MealOrderSummaryScreen()
            MunchBoxApp()
        }
    }
}
