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
    var lazeez = Restaurant(name ="Lazeez", setOf(), imageID = R.drawable.lazeez )
    var campusPizza = Restaurant(name="Campus Pizza", setOf(), imageID = R.drawable.campuspizza)
    var shawaramaPlus = Restaurant(name="Shawarma Plus", setOf(), imageID = R.drawable.shawarmaplus)

//    val lazeezMeal = Meal(setOf(DietaryOption.VEGE, DietaryOption.GF), lazeez, setOf(DayOfWeek.SUNDAY), mapOf(Pair(DayOfWeek.SUNDAY, 20)))
//    val lazeezMeal2 = Meal(setOf(DietaryOption.HALAL, DietaryOption.MEAT), lazeez, setOf(DayOfWeek.SUNDAY))
//    val campusPizzaMeal =  Meal(setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL, DietaryOption.MEAT), campusPizza, setOf(DayOfWeek.TUESDAY))
//    val campusPizzaMeal2 =  Meal(setOf(DietaryOption.GF), campusPizza, setOf(DayOfWeek.TUESDAY))
//    val shawarmaPlusMeal =  Meal(setOf(DietaryOption.HALAL, DietaryOption.MEAT), shawaramaPlus, setOf(DayOfWeek.SUNDAY))
//    val shawarmaPlusMeal2 =  Meal(setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL), shawaramaPlus, setOf(DayOfWeek.WEDNESDAY))

    val allMeals = setOf(lazeezMeal, lazeezMeal2, campusPizzaMeal, campusPizzaMeal2, shawarmaPlusMeal, shawarmaPlusMeal2)
    val pickUpOptions = setOf(DayOfWeek.TUESDAY)
}