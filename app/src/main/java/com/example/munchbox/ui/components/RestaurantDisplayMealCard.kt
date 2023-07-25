package com.example.munchbox.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchbox.R
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.ui.theme.Typography
import java.util.Date

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

//    RestaurantDisplayMealCard(
//        meal = lazeezMeal,
//        name = "Rox",
//        today = DayOfWeek.SUNDAY,
//        onCancelCallback = { },
//        modifier = Modifier
//    )
}
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDisplayMealCard(meal : Meal,
                              name : String,
                              today : DayOfWeek,
                              onCancelCallback : (Meal) -> Unit,
                              modifier : Modifier) {
    var expanded = remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        onClick = { expanded.value = !expanded.value}
    )
    {
        Column(modifier = modifier.padding(24.dp)) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {
                Text(
                    text = name,
                    style = Typography.headlineMedium,
                )
                if ( expanded.value ) {
                    Icon(
                        imageVector = Icons.Filled.ExpandLess,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
                else {
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
            if(meal.cancelledOnDate > Date(0)){
                Text(
                    text = "This meal has been cancelled.",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Today's Orders",
                style = Typography.labelSmall,
            )
            Text(
                text = if (!meal.amountOrdered.contains(today)) {
                    "0"
                }
                else {
                    meal.amountOrdered[today].toString()
                },
                style = Typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total Orders",
                style = Typography.labelSmall,
            )
            Text(
                text = meal.totalOrders.toString(),
                style = Typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dietary Options",
                style = Typography.labelSmall,
            )
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
            if (expanded.value) {
                Spacer(modifier = Modifier.height(8.dp))
                for (day in meal.days) {
                    Text(
                        text = day.str,
                        style = Typography.labelSmall,
                    )
                    Text(
                        text = if (!meal.amountOrdered.contains(day)) {
                            "0"
                        }
                        else {
                            meal.amountOrdered[day].toString()
                        },
                        style = Typography.labelSmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if(meal.cancelledOnDate == Date(0)){
                    Button(
                        onClick = {
                            onCancelCallback(meal)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel Meal")
                    }
                }
            }

        }
    }
}