package com.example.munchbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        setContent {
            MunchBoxApp()
//            MealCardContainer()
//            NumberOfMealsScreen()
//            OrderSummaryCard(OrderUiState(restaurant = Restaurant("Lazeez", setOf())))
//            MealOrderSummaryScreen()
        }
    }
}
