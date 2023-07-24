package com.example.munchbox.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.StorageServices
import com.example.munchbox.ui.components.DayTabs
import com.example.munchbox.ui.components.MealCard
import java.util.Date

@Composable
fun MealSelectionScreen(
    storageServices: StorageServices,
    restaurants: Set<Restaurant>,
    orderInfo: OrderUiState,
    numMealsRequired: Int,
    onCancelButtonClicked: () -> Unit = {},
    onSubmit: (Array<Meal?>) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * State Variables
     */

    var selectedDay by remember { mutableStateOf(DayOfWeek.SATURDAY) }

    fun getAvailableRestaurants(restaurants : Set<Restaurant>) : Set<Restaurant> {
        var availableRestaurants : Set<Restaurant> = setOf()

        for (restaurant in restaurants) {
            for(meal in restaurant.meals){
                Log.d("HELLO_" + restaurant.restaurantID, meal.mealID)
                for(day in meal.days){
                    Log.d("HELLO_ON", day.str)
                }
                Log.d("HELLO_DAYS_DONE","")
            }
            if (restaurant.meals.filter { meal: Meal -> meal.days.contains(selectedDay) }.isNotEmpty()) {
                availableRestaurants = availableRestaurants.plus(restaurant)
            }
        }
        return availableRestaurants
    }


    fun getAvailableMeals(meals: Set<Meal>, selectedOptions: Set<DietaryOption>): Set<Meal> {
        var availableMeals = setOf<Meal>()
        for (meal in meals.asIterable()) {
            if (meal.options.containsAll(selectedOptions) && meal.cancelledOnDate == Date(0)) {
                availableMeals = availableMeals.plus(meal)
            }
        }
        return availableMeals
    }

    fun getAvailableOptions(meals: Set<Meal>, selectedOptions: Set<DietaryOption>): Set<DietaryOption> {
        // Get the available meals based on the added and selected options
        val availableMeals: Set<Meal> = getAvailableMeals(meals, selectedOptions)
        var availableOptions: Set<DietaryOption> = setOf()

        for (meal in availableMeals.iterator()) {
            availableOptions = availableOptions.plus(meal.options)
        }
        return availableOptions
    }

    var availableRestaurants by remember { mutableStateOf(getAvailableRestaurants(restaurants = restaurants)) }
    var orderedMeals by remember { mutableStateOf(Array<Meal?>(7) { null }) }
    var numOrderedMeals by remember { mutableStateOf(0) }

    /**
     * Callback functions
     */

    fun onSelectCallback (selectedOptions : Set<DietaryOption>, option : DietaryOption) : Set<DietaryOption> {
        val ret = if (!selectedOptions.contains(option)) {
            selectedOptions.plus(option)
        } else {
            selectedOptions.minus(option)
        }

        return ret
    }

    fun onDaySelectorClick(day: DayOfWeek) {
        selectedDay = day
        availableRestaurants = getAvailableRestaurants(restaurants)
    }

    fun getRestaurantMeals(restaurant : Restaurant, day : DayOfWeek) : Set<Meal> {
        var mealsToday : Set<Meal> = setOf()
        // Add all meals that are offered by this restaurant on this day
        mealsToday = mealsToday.plus(restaurant.meals.filter { meal: Meal -> meal.days.contains(day) }.filter { meal: Meal -> meal.cancelledOnDate == Date(0) })
        return mealsToday
    }

    fun recordMealAddition(meal : Meal) {
        if (orderedMeals[selectedDay.id] == meal) {
            orderedMeals[selectedDay.id] = null
            numOrderedMeals--
        }
        else {
            orderedMeals[selectedDay.id] = meal
            numOrderedMeals++
        }
    }

    // TODO: This is never called and breaks the code due to Meal no longer having restaurant so I commented it
//    fun getAddedOptions(restaurant: Restaurant, todaysMeal : Meal?) : Set<DietaryOption> {
//        if (todaysMeal == null || todaysMeal.restaurant != restaurant) {
//            return setOf()
//        }
//
//        return todaysMeal.options
//    }
    /**
     * Composables
     */

    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        DayTabs(days = DayOfWeek.values(), selectedTabIndex = selectedDay.id) { num ->
            onDaySelectorClick(
                num
            )
        }
        if (availableRestaurants.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Oops! No meals today",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        availableRestaurants.forEach {
            var initSelect : Set<DietaryOption> = setOf()
            if (it.meals.contains(orderedMeals[selectedDay.id])) {
                initSelect = initSelect.plus(orderedMeals[selectedDay.id]!!.options)
            }

            var added by remember(selectedDay) { mutableStateOf(it.meals.contains(orderedMeals[selectedDay.id])) }
            var allMeals by remember(selectedDay) { mutableStateOf(it.meals.filter { meal : Meal -> meal.days.contains(selectedDay) }.toSet()) }
            var selectedOptions by remember(selectedDay) { mutableStateOf(initSelect) }
            var availableOptions by remember(selectedDay) { mutableStateOf(getAvailableOptions(allMeals, selectedOptions))}

            Spacer(modifier = Modifier.height(13.dp))
            MealCard (
                storageServices = storageServices,
                restaurantName = it.name, // TODO: replace this with the actual restaurant name
                imageID = it.imageID,
                allMeals = allMeals,
                onAdd = { meal ->
                    recordMealAddition(meal)
                    added = !added
                    if(added){
                        orderInfo.unorderedSelectedPickupDay[meal] = selectedDay
                    }
                    if (!added) {
                        selectedOptions = setOf()
                        availableOptions = getAvailableOptions(allMeals, selectedOptions)
                        orderInfo.unorderedSelectedPickupDay.remove(meal)
                    }
                    else {
                        selectedOptions = meal.options
                    }
                },
                onSelectOption = { option : DietaryOption ->
                    selectedOptions = onSelectCallback(selectedOptions, option)
                    availableOptions = getAvailableOptions(allMeals, selectedOptions)
                },
                selectedOptions = selectedOptions,
                availableOptions = availableOptions,
                added = added,
                disabled = !added && (orderedMeals[selectedDay.id] != null || numOrderedMeals >= numMealsRequired),
                onConfirmButtonClick = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(13.dp))
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
                enabled = numOrderedMeals == numMealsRequired,
                onClick = { onSubmit(orderedMeals) }
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }
}