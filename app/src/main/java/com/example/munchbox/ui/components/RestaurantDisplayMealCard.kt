package com.example.munchbox.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.data.DataSource.lazeezMeal
import com.example.munchbox.ui.theme.Typography

@Preview(
    showBackground = true,
    showSystemUi = true,
    name  = "Selection Card"
)
@Composable
fun t1() {
    var selectedOptions = setOf<DietaryOption>()
    var selectedDays = setOf<DayOfWeek>()
    var added : Boolean = false

    RestaurantDisplayMealCard(
        meal = lazeezMeal,
        name = "Rox",
        today = DayOfWeek.SUNDAY,
        onCancelCallback = { },
        modifier = Modifier
    )
}
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDisplayMealCard(meal : Meal,
                              name : String,
                              today : DayOfWeek,
                              onCancelCallback : () -> Unit,
                              modifier : Modifier) {
    var expanded = remember { mutableStateOf(true) }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        onClick = { expanded.value = !expanded.value}
    )
    {
        Column(modifier = modifier.padding(25.dp)) {
            Text(
                text = name,
                style = Typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Today's Orders",
                style = Typography.labelSmall,
            )
            Text(
                text = meal.orders[today].toString(),
                style = Typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Orders",
                style = Typography.labelSmall,
            )
            Text(
                text = meal.totalOrderCount().toString(),
                style = Typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (expanded.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dietary Options",
                    style = Typography.labelSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (option in meal.options) {
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text(option.str) }
                        )
                        Spacer(modifier = Modifier.size(4.dp, 4.dp))
                    }
                }

                for (entry in meal.orders) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.key.str,
                        style = Typography.labelSmall,
                    )
                    Text(
                        text = entry.value.toString(),
                        style = Typography.labelSmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedButton(
                    onClick = {
                        onCancelCallback()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Meal")
                }
            }

        }
    }
}