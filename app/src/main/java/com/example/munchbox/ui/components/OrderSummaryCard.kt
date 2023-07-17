package com.example.munchbox.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.munchbox.R
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant



@Composable
fun OrderSummaryCard(meal: Meal, modifier: Modifier = Modifier.fillMaxWidth()){
    MealCard(
        restaurant = meal.restaurant,
        allMeals = setOf(meal),
        onAdd = { null },
        onSelectOption = { null },
        selectedOptions = meal.options,
        availableOptions = meal.options,
        added = true,
        disabled = true,
        modifier = modifier
            .fillMaxWidth()
    )
}
@Preview
@Composable
fun PreviewOrderSummaryCard(){
    val lazeez = Restaurant("1","Lazeez", setOf(), R.drawable.lazeez)
    lazeez.addMeals(setOf(Meal(setOf(DietaryOption.HALAL), lazeez, setOf(DayOfWeek.SUNDAY))))

    for (meal in lazeez.meals) {
        OrderSummaryCard(meal, Modifier.fillMaxWidth())
    }
}


