package com.example.munchbox.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.R
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant



@Composable
fun OrderSummaryCard(meal: Meal){
    MealCard(
        restaurant = meal.restaurant,
        allMeals = setOf(meal),
        onAdd = { null },
        added = meal.options,
        disabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp)
    )
}
@Preview
@Composable
fun PreviewOrderSummaryCard(){
    val lazeez = Restaurant("Lazeez", setOf(), R.drawable.lazeez)
    lazeez.addMeals(setOf(Meal(setOf(DietaryOption.HALAL), lazeez, setOf(DayOfWeek.SUNDAY))))

    for (meal in lazeez.meals) {
        OrderSummaryCard(meal)
    }
}


