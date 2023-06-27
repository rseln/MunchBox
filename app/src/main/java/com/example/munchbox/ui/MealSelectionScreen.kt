package com.example.munchbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.munchbox.R
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.ui.components.DayTabs
import com.example.munchbox.ui.components.MealCard

@Composable
fun MealSelectionScreen(
    restaurants: Set<Restaurant>,
    onCancelButtonClicked: () -> Unit = {},
    onNextButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    fun getAvailableRestaurants(restaurants : Set<Restaurant>, day : DayOfWeek) : Set<Restaurant> {
        var availableRestaurants : Set<Restaurant> = setOf()

        for (restaurant in restaurants) {
            if (restaurant.meals.filter { meal: Meal -> meal.days.contains(day) }.isNotEmpty()) {
                availableRestaurants = availableRestaurants.plus(restaurant)
            }
        }
        return availableRestaurants
    }

    fun getRestaurantMeals(restaurant : Restaurant, day : DayOfWeek) : Set<Meal> {
        var todaysMeals : Set<Meal> = setOf()
        // Add all meals that are offered by this restaurant on this day
        todaysMeals = todaysMeals.plus(restaurant.meals.filter { meal: Meal -> meal.days.contains(day) })
        return todaysMeals
    }

    fun getAddedOptions(restaurant: Restaurant, todaysMeal : Meal?) : Set<DietaryOption> {
        if (todaysMeal == null || todaysMeal.restaurant != restaurant) {
            return setOf()
        }

        return todaysMeal.options
    }

    /**
     * State Variables
     */
    var selectedDay by remember { mutableStateOf(DayOfWeek.SATURDAY) }
    var availableRestaurants by remember { mutableStateOf(getAvailableRestaurants(restaurants = restaurants, day = selectedDay)) }
    var orderedMeals by remember { mutableStateOf(Array<Meal?>(7) { null }) }
    // A set of pairs of restaurants and the selected dietary options
    var selectedOptionsSet by remember { mutableStateOf( mapOf<Restaurant, Set<DietaryOption>>()) }


    /**
     * Callback functions
     */
    fun onDaySelectorClick(day: DayOfWeek) {
        selectedDay = day
        availableRestaurants = getAvailableRestaurants(restaurants, day)
        // Reset all the selected options
        selectedOptionsSet = mapOf<Restaurant, Set<DietaryOption>>()
    }

    fun recordMealAddition(meal : Meal) {
        if (orderedMeals[selectedDay.id] == meal) {
            orderedMeals[selectedDay.id] = null
        }
        else {
            orderedMeals[selectedDay.id] = meal
        }
    }

    fun onSelectCallback (option : DietaryOption, it : Restaurant) {
        // Get the corresponding restaurant/selected options pair
        var selectedOptions = selectedOptionsSet[it]
        if (selectedOptions == null) {
            selectedOptions = setOf(option)
        }
        else if (selectedOptions.contains(option)) {
            selectedOptions = selectedOptions.minus(option)
        }
        else {
            selectedOptions = selectedOptions.plus(option)
        }
        selectedOptionsSet = selectedOptionsSet.minus(it)
        selectedOptionsSet = selectedOptionsSet.plus(Pair(it, selectedOptions))
    }

    /**
     * Composables
     */
    Column {
        DayTabs(days = DayOfWeek.values(), selectedTabIndex = selectedDay.id) { num -> onDaySelectorClick(num) }

        availableRestaurants.forEach {
            var added by remember { mutableStateOf(it.meals.contains(orderedMeals[selectedDay.id])) }

            selectedOptionsSet = selectedOptionsSet.plus(Pair(it, setOf()))

            MealCard(
                restaurant = it,
                allMeals = getRestaurantMeals(it, selectedDay),
                onAddCallback = { meal -> recordMealAddition(meal)
                    added = !added },
                onSelectCallback = { option : DietaryOption -> onSelectCallback(option, it) },
                selectedOptions = selectedOptionsSet.get(it)!!,
                added = added && it.meals.contains(orderedMeals[selectedDay.id]),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedButton(modifier = Modifier.weight(1f), onClick = onCancelButtonClicked) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                modifier = Modifier.weight(1f),
                // the button is enabled when the user makes a selection
    //            enabled = selectedValue.isNotEmpty(),
                onClick = onNextButtonClicked
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }
}