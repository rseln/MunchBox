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
    /**
     * State Variables
     */
    var selectedDay by remember { mutableStateOf(DayOfWeek.SATURDAY) }

    fun getAvailableRestaurants(restaurants : Set<Restaurant>) : Set<Restaurant> {
        var availableRestaurants : Set<Restaurant> = setOf()

        for (restaurant in restaurants) {
            if (restaurant.meals.filter { meal: Meal -> meal.days.contains(selectedDay) }.isNotEmpty()) {
                availableRestaurants = availableRestaurants.plus(restaurant)
            }
        }
        return availableRestaurants
    }

    var availableRestaurants by remember { mutableStateOf(getAvailableRestaurants(restaurants = restaurants)) }
    var orderedMeals by remember { mutableStateOf(Array<Meal?>(7) { null }) }

    /**
     * Callback functions
     */


    fun onDaySelectorClick(day: DayOfWeek) {
        selectedDay = day
        availableRestaurants = getAvailableRestaurants(restaurants)
    }

    fun getRestaurantMeals(restaurant : Restaurant, day : DayOfWeek) : Set<Meal> {
        var mealsToday : Set<Meal> = setOf()
        // Add all meals that are offered by this restaurant on this day
        mealsToday = mealsToday.plus(restaurant.meals.filter { meal: Meal -> meal.days.contains(day) })
        return mealsToday
    }

    fun recordMealAddition(meal : Meal) {
        if (orderedMeals[selectedDay.id] == meal) {
            orderedMeals[selectedDay.id] = null
        }
        else {
            orderedMeals[selectedDay.id] = meal
        }
    }


    fun getAddedOptions(restaurant: Restaurant, todaysMeal : Meal?) : Set<DietaryOption> {
        if (todaysMeal == null || todaysMeal.restaurant != restaurant) {
            return setOf()
        }

        return todaysMeal.options
    }
    /**
     * Composables
     */
    Column(modifier = modifier) {
        DayTabs(days = DayOfWeek.values(), selectedTabIndex = selectedDay.id) { num ->
            onDaySelectorClick(
                num
            )
        }
        availableRestaurants.forEach {

            var addedOptions by remember { mutableStateOf(getAddedOptions(it, orderedMeals[selectedDay.id])) }
            MealCard(
                restaurant = it,
                allMeals = getRestaurantMeals(it, selectedDay),
                onAdd = { meal ->
                    recordMealAddition(meal)
                    addedOptions = getAddedOptions(it, orderedMeals[selectedDay.id])
                        },
                added = addedOptions,
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