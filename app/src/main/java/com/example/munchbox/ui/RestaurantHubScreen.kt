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
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.DataSource.currentDay
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.ui.components.RestaurantAddMealCard
import com.example.munchbox.ui.components.RestaurantDisplayMealCard
import com.example.munchbox.ui.components.SelectCard

@Composable
fun RestaurantHubScreen(
    orderUiState: OrderUiState,
    restaurant : Restaurant,
    modifier: Modifier = Modifier,
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedOptions = remember { mutableStateOf(setOf<DietaryOption>()) }
    val selectedDays = remember { mutableStateOf(setOf<DayOfWeek>()) }
    val availableMeals = remember { mutableStateOf(orderUiState.meals) }
    val scrollState = rememberScrollState()

    fun addNewMeal(newDietaryOptions : Set<DietaryOption>, newDays : Set<DayOfWeek>) {
        val meal : Meal = Meal(newDietaryOptions, restaurant, newDays)
        availableMeals.value = availableMeals.value.plus(meal)
        selectedDays.value = setOf<DayOfWeek>()
        selectedOptions.value = setOf<DietaryOption>()
    }
    fun cancelMeal(meal : Meal) {
        availableMeals.value = availableMeals.value.minus(meal)
    }

    fun cancelAdd() {
        expanded.value = false
        selectedDays.value = setOf<DayOfWeek>()
        selectedOptions.value = setOf<DietaryOption>()
    }

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
                        addNewMeal(newDietaryOptions, newDays)
                    },
                    onCancelCallback = { cancelAdd() },
                    modifier = Modifier
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        MealSummary(
            availableMeals.value,
            {meal : Meal -> cancelMeal(meal)},
            modifier
        )
    }
}

@Composable
fun MealSummary(meals : Set<Meal>, onCancelCallback : (Meal) -> Unit, modifier: Modifier = Modifier){
    var usedNicknames : Set<String> = setOf()

    fun getMealNickname(meal : Meal, allMeals : Set<Meal>) : String {
        var nickname : String
        val filteredAllMeals : Set<Meal> = allMeals.minus(meal)
        var allUsedDietaryOptions : Set<DietaryOption> = setOf()

        // Find if any of the dietary options are unique
        for (otherMeals in filteredAllMeals) {
            allUsedDietaryOptions = allUsedDietaryOptions.union(otherMeals.options)
        }
        val unusedDietaryOptions = meal.options.minus(allUsedDietaryOptions)

        // If we already used the nickname, then number it
        if (unusedDietaryOptions.isEmpty()) {
            var idx = 1
            nickname = meal.options.first().str + " " + idx.toString()

            while (usedNicknames.contains("$nickname $idx")) {
                idx++
                nickname = meal.options.first().str + " " + idx.toString()
            }

            usedNicknames = usedNicknames.plus(nickname)
            return nickname
        }

        // Otherwise, return the unique nickname based on dietary option
        nickname = unusedDietaryOptions.first().str
        return nickname
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (meals.isNotEmpty()) {
            Row{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Text(
                        text = "Meal Summary",
                        style = MaterialTheme.typography.headlineMedium,
                    )

                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        for (meal in meals) {
            RestaurantDisplayMealCard(
                meal = meal,
                name = getMealNickname(meal, meals),
                today = currentDay,
                onCancelCallback = onCancelCallback,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}
