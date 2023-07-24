package com.example.munchbox.data

import com.example.munchbox.R
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant

object DataSource {
    val currentDay = DayOfWeek.TUESDAY

    val quantityOptions = listOf(
        Triple(R.string.meals_1, 1, 10),
        Triple(R.string.meals_3, 3, 27),
        Triple(R.string.meals_5, 5, 42)
    )

    /** Fake Restaurants to use for now.**/
    //TODO: delete this once we cana create a restaurant.
    var lazeez = Restaurant("lazeez_123", name ="Lazeez", setOf(), imageID = R.drawable.lazeez )
}