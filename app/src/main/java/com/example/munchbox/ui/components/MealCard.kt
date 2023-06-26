@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.munchbox.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.ui.theme.Typography


@Preview(
    showBackground = true,
    showSystemUi = true,
    name  = "Selection Card Preview"
)
@Composable
fun MealCardContainer() {

    val restaurant = Restaurant("Lazeez", setOf())
    val vegeMeal = Meal(setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL), restaurant, setOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val meatMeal = Meal(setOf(DietaryOption.HALAL, DietaryOption.MEAT), restaurant, setOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val allMeals = setOf(vegeMeal, meatMeal)
    restaurant.addMeals(allMeals)

    var added by remember { mutableStateOf(setOf<DietaryOption>()) }
    var selectedMeals : Set<Meal> = setOf()

    fun onAddCallback(meal: Meal) {
        added = meal.options
        selectedMeals = selectedMeals.plus(meal)
    }

    Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MealCard(
            restaurant = restaurant,
            allMeals = allMeals,
            onAdd = { meal: Meal -> onAddCallback(meal) },
            added = added,
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp))
    }
}
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MealCard(restaurant: Restaurant, allMeals: Set<Meal>, onAdd: (Meal) -> Unit, added: Set<DietaryOption>, modifier: Modifier = Modifier) {

    fun getAvailableMeals(meals: Set<Meal>, selectedOptions: Set<DietaryOption>): Set<Meal> {
        var availableMeals = setOf<Meal>()
        for (meal in meals.asIterable()) {
            if (meal.options.containsAll(selectedOptions)) {
                availableMeals = availableMeals.plus(meal)
            }
        }
        return availableMeals
    }

    fun getAvailableOptions(meals: Set<Meal>, selectedOptions: Set<DietaryOption>): Set<DietaryOption> {
        // Get the available meals based on the added and selected options
        val availableMeals: Set<Meal> = getAvailableMeals(meals, selectedOptions.plus(added))
        var availableOptions: Set<DietaryOption> = setOf()

        for (meal in availableMeals.iterator()) {
            availableOptions = availableOptions.plus(meal.options)
        }
        return availableOptions
    }

    fun getSelectedMeal(meals: Set<Meal>, selectedOptions: Set<DietaryOption>): Meal {
        val availableMeals: Set<Meal> = getAvailableMeals(meals, selectedOptions)

        return availableMeals.elementAt(0)
    }

    /**
     * State variables
     */
    var selectedOptions by remember { mutableStateOf(added) }
    var availableOptions by remember {
        mutableStateOf( getAvailableOptions(
            allMeals,
            selectedOptions
        ))
    }

    /**
     * CALLBACKS
     */
    fun onSelectCallback (option : DietaryOption) {
        selectedOptions = if (!selectedOptions.contains(option)) {
            selectedOptions.plus(option)
        } else {
            selectedOptions.minus(option)
        }

        availableOptions = getAvailableOptions(allMeals, selectedOptions)
    }

    /**
     * UI Components
     */
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large)
    {
        Image(
            painter = ColorPainter(MaterialTheme.colorScheme.background),
            contentDescription = "Test",
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .fillMaxWidth()
                .aspectRatio(3f / 2f)
        )
        Column( modifier = modifier) {
            Text(
                text = restaurant.name,
                style = Typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dietary Options",
                style = Typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (option in availableOptions) {
                    FilterChip(
                        selected = selectedOptions.contains(option),
                        onClick = {
                            if (added.isEmpty()) {
                                onSelectCallback(option)
                            }
                        },
                        label = { Text(option.str) },
                        leadingIcon = if (selectedOptions.contains(option)) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Localized Description",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        }
                    )
                    Spacer(modifier = Modifier.size(4.dp, 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            ElevatedButton(
                onClick = {
                    onAdd(getSelectedMeal(allMeals, selectedOptions))
                },
            ) {
                if (added.isEmpty()) {
                    Text("Add Meal")
                }
                else {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Localized Description",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                    Text("Meal Added")
                }

            }
        }

    }
}