@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.munchbox.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Order
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.StorageServices
import com.example.munchbox.ui.theme.Typography
import kotlinx.coroutines.launch
import java.util.Date


@Preview(
    showBackground = true,
    showSystemUi = true,
    name  = "Selection Card Preview"
)
@Composable
fun MealCardContainer() {

    val restaurant = Restaurant("1","Lazeez", setOf())
    val vegeMeal = Meal("meal_id", "rest_id", setOf(DietaryOption.VEGETARIAN, DietaryOption.GLUTEN_FREE, DietaryOption.HALAL), setOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val meatMeal = Meal("meal_id", "rest_id", setOf(DietaryOption.HALAL, DietaryOption.MEAT), setOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val allMeals = setOf(vegeMeal, meatMeal)
    restaurant.addMeals(allMeals)
}
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MealCard(storageServices: StorageServices,
             restaurantName: String,
             imageID: String?,
             allMeals: Set<Meal>,
             order: Order = Order("-1", "-1", "-1", "-1", Date(), false),
             onAdd: (Meal) -> Unit,
             onSelectOption: (DietaryOption) -> Unit,
             selectedOptions : Set<DietaryOption>,
             availableOptions : Set<DietaryOption>,
             added: Boolean,
             disabled: Boolean = false,
             confirmDisabled: Boolean = true,
             onConfirmButtonClick: () -> Unit,
             modifier: Modifier = Modifier) {
    fun getAvailableMeals(meals: Set<Meal>, selectedOptions: Set<DietaryOption>): Set<Meal> {
        var availableMeals = setOf<Meal>()
        for (meal in meals.asIterable()) {
            if (meal.options.containsAll(selectedOptions)) {
                availableMeals = availableMeals.plus(meal)
            }
        }
        return availableMeals
    }

    fun getSelectedMeal(meals: Set<Meal>, selectedOptions: Set<DietaryOption>): Meal {
        val availableMeals: Set<Meal> = getAvailableMeals(meals, selectedOptions)
        return availableMeals.elementAt(0)
    }

    /**
     * CALLBACKS
     */
    fun onSelectCallback (option : DietaryOption) {
        onSelectOption(option)
    }

    /**
     * COROUTINE FOR API CALLS
     */
    val orderService = storageServices.orderService()

    val coroutineScope = rememberCoroutineScope()
    val updateOrderPickedUpOnClick: () -> Unit = {
        coroutineScope.launch {
            val orderId = order.orderID
            orderService.updateOrderPickedUpByOrderID(orderId, true)
        }
    }

    /**
     * UI Components
     */

    val isConfirmEnabled = remember { mutableStateOf(true) }
    val isConfirmed = remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large)
    {
        if(imageID != null){
            AsyncImage(model = imageID, contentDescription = null, modifier = Modifier.fillMaxSize())
        }
        Column( modifier = modifier.padding(24.dp)) {
            Text(
                text = restaurantName,
                style = Typography.headlineMedium,
            )

            if (order.orderID.length > 4) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Order Confirmation Number",
                    style = Typography.labelSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text (
                    text = order.orderID.slice(IntRange(6,9)).uppercase(),
                    style = MaterialTheme.typography.titleSmall
                )
            }
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
                            if (!added && !disabled) {
                                onSelectCallback(option)
                            }
                        },
                        label = { Text(option.str) },
                        leadingIcon = if (selectedOptions.contains(option) && !added) {
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
            if (!disabled) {
                ElevatedButton(
                    onClick = {
                        if (selectedOptions.isNotEmpty()) {
                            onAdd(getSelectedMeal(allMeals, selectedOptions))
                        }
                    },
                ) {
                    if (!added) {
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

            if (!confirmDisabled) {
                ElevatedButton(
                    enabled = isConfirmEnabled.value,
                    onClick = {
                        if (isConfirmEnabled.value) isConfirmEnabled.value = false
                        isConfirmed.value = true

                        //update the database
                        updateOrderPickedUpOnClick()

                        // for nav + ui changes
                        onConfirmButtonClick()
                    },
                ) {
                    if (!isConfirmed.value) {
                        Text("Confirm Pickup")
                    }
                    else {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Localized Description",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                        Text("Pickup Confirmed")
                    }
                }
            }
        }

    }
}