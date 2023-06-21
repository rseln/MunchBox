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
import androidx.compose.runtime.saveable.rememberSaveable
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
fun RestaurantCardPreview() {
    val restaurant = Restaurant("Lazeez")
    val vegeMeal = Meal(setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL), restaurant, setOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val meatMeal = Meal(setOf(DietaryOption.HALAL, DietaryOption.MEAT), restaurant, setOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val allMeals = setOf<Meal>(vegeMeal, meatMeal)


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
        var availableMeals: Set<Meal> = getAvailableMeals(meals, selectedOptions)
        var availableOptions: Set<DietaryOption> = setOf<DietaryOption>()

        for (meal in availableMeals.iterator()) {
            availableOptions = availableOptions.plus(meal.options)
        }
        return availableOptions
    }

    var selectedOptions by remember { mutableStateOf(setOf<DietaryOption>()) }
    var availableOptions by remember {
        mutableStateOf( getAvailableOptions(
            allMeals,
            selectedOptions
        ))
    }

    var name by rememberSaveable { mutableStateOf(1) }

    fun onSelectCallback (option : DietaryOption) {
        if (!selectedOptions.contains(option)) {
            selectedOptions = selectedOptions.plus(option)
        }
        else {
            selectedOptions = selectedOptions.minus(option)
        }

        availableOptions = getAvailableOptions(allMeals, selectedOptions)

        name += 1
    }


    Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MealCard(
            name = name,
            options = availableOptions,
            restaurant = restaurant,
            selected = selectedOptions,
            onSelect = { option ->
                       onSelectCallback(option)
                       },
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp))
    }
}
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MealCard(name: Int, options: Set<DietaryOption>, selected: Set<DietaryOption>, restaurant: Restaurant, onSelect: (DietaryOption) -> Unit, modifier: Modifier = Modifier) {

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
            var selectedMeal : Meal? = null

            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (option in options) {
                    FilterChip(
                        selected = selected.contains(option),
                        onClick = {
                            onSelect(option)
                        },
                        label = { Text(option.str) },
                        leadingIcon = if (selected.contains(option)) {
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
            var added = false
            ElevatedButton(
                onClick = {
                    added = !added

                }
            ) {
                if (!added) {
                    Text("Add Meal")
                }
                else {
                    Text("Meal Added")
                }

            }
        }

    }
}