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
fun OrderSummaryCard(meal: Meal,
                     confirmDisabled: Boolean,
                     onConfirmButtonClick: () -> Unit = {},
                     modifier: Modifier = Modifier.fillMaxWidth()){
    // TODO: Replace restaurant name with the actual restaurant name from DB
    MealCard(
        restaurantName = "Temporary Lazeez",
        allMeals = setOf(meal),
        onAdd = { null },
        onSelectOption = { null },
        selectedOptions = meal.options,
        availableOptions = meal.options,
        added = true,
        disabled = true,
        confirmDisabled = confirmDisabled,
        onConfirmButtonClick = onConfirmButtonClick,
        modifier = modifier
            .fillMaxWidth()
    )
}
@Preview
@Composable
fun PreviewOrderSummaryCard(){
    val lazeez = Restaurant("1", "Lazeez", setOf(), R.drawable.lazeez)
//    lazeez.addMeals(setOf(Meal("lazeez_meal", "lazeez_id", "lazeez", setOf(DietaryOption.HALAL), setOf(DayOfWeek.SUNDAY))))

    for (meal in lazeez.meals) {
        OrderSummaryCard(meal, false, {}, Modifier.fillMaxWidth())
    }
}


