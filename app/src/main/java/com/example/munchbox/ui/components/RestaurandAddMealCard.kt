package com.example.munchbox.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.ui.theme.Typography

@Preview(
    showBackground = true,
    showSystemUi = true,
    name  = "Selection Card"
)
@Composable
fun t() {
    var selectedOptions = setOf<DietaryOption>()
    var selectedDays = setOf<DayOfWeek>()
    var added : Boolean = false

    RestaurantAddMealCard(
        selectedOptions = selectedOptions,
        selectedDays = selectedDays,
        added = added,
        onSelectOptionCallback = { option : DietaryOption ->
            if (selectedOptions.contains(option)) {
                selectedOptions = selectedOptions.minus(option)
            }
            else {
                selectedOptions = selectedOptions.plus(option)
            }
        },
        onSelectDayCallback = {day : DayOfWeek ->
            if (selectedDays.contains(day)) {
                selectedDays = selectedDays.minus(day)
            }
            else {
                selectedDays = selectedDays.plus(day)
            }
        },
        onAddCallback = { options : Set<DietaryOption>, days : Set<DayOfWeek> ->
            added = true
                        },
        onCancelCallback = { added = false },
        modifier = Modifier
    )
}
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RestaurantAddMealCard(selectedOptions : Set<DietaryOption>,
                selectedDays : Set<DayOfWeek>,
                added : Boolean,
                onSelectOptionCallback : (DietaryOption) -> Unit,
                onSelectDayCallback : (DayOfWeek) -> Unit,
                onAddCallback : (Set<DietaryOption>, Set<DayOfWeek>) -> Unit,
                onCancelCallback : () -> Unit,
                modifier : Modifier) {
    val availableOptions : Set<DietaryOption> = DietaryOption.values().toSet()
    val availableDays : Set<DayOfWeek> = DayOfWeek.values().toSet()

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    )
    {
        Column(modifier = modifier.padding(25.dp)) {
            Text(
                text = "New Meal",
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
                            if (!added) {
                                onSelectOptionCallback(option)
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dietary Options",
                style = Typography.labelSmall,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (day in availableDays) {
                    FilterChip(
                        selected = selectedDays.contains(day),
                        onClick = {
                            if (!added) {
                                onSelectDayCallback(day)
                            }
                        },
                        label = { Text(day.str) },
                        leadingIcon = if (selectedDays.contains(day)) {
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
                    if (selectedOptions.isNotEmpty() && selectedDays.isNotEmpty()) {
                        onAddCallback(selectedOptions, selectedDays)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Meal")
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = {
                    onCancelCallback()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }

        }
    }
}