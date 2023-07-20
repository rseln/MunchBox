package com.example.munchbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.munchbox.ui.theme.MunchBoxTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MunchBoxTheme {
                MunchBoxApp()
            }

//            MealCardContainer()
//            NumberOfMealsScreen()
//            OrderSummaryCard(OrderUiState(restaurant = Restaurant("Lazeez", setOf())))
//            MealOrderSummaryScreen()
        }
    }
}
