package com.example.greetingcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.example.greetingcard.ui.theme.GreetingCardTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greetingcard.ui.theme.Typography
import androidx.compose.material3.ElevatedButton

enum class DietaryOption(val id: Int, val str : String) {
    HALAL(0, "Halal"),
    VEGE(1, "Vegetarian"),
    KOSHER(2, "Kosher"),
    VEGAN(3, "Vegan"),
    GF(4, "Gluten Free"),
    PEANUT_FREE(5, "Peanut Free"),
    MEAT(6, "Meat")
}

class Meal(options : Array<DietaryOption>) {
    fun optionToStr(option : DietaryOption): String {
        return option.str
    }
}

class Restaurant(name : String) {
    val meals : MutableList<Meal> = mutableListOf()
    fun addMeal(meal : Meal) {
        meals.add(meal)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreetingCardTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name  = "Selection Card Preview"
)
@Composable
fun RestaurantCardPreview() {
    Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RestaurantCard(name = "Lazeez", modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantCard(name: String, modifier: Modifier = Modifier) {
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
                    text = "$name",
                    style = Typography.headlineMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dietary Options",
                    style = Typography.labelSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                var selected = false
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selected,
                        onClick = { selected = !selected },
                        label = { Text("Filter chip") },
                        leadingIcon = if (selected) {
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