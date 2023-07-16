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

    val lazeezMeal = Meal("lazeez_meal_1", "lazeez_id", "Lazeez", setOf(DietaryOption.VEGE, DietaryOption.GF), setOf(DayOfWeek.SUNDAY), mapOf(Pair(DayOfWeek.SUNDAY, 20)))
    val lazeezMeal2 = Meal("lazeez_meal_2", "lazeez_id", "Lazeez", setOf(DietaryOption.HALAL, DietaryOption.MEAT), setOf(DayOfWeek.SUNDAY))
    val campusPizzaMeal =  Meal("campus_pizza_meal_1", "campus_pizza_id", "Campus Pizza", setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL, DietaryOption.MEAT), setOf(DayOfWeek.TUESDAY))
    val campusPizzaMeal2 =  Meal("campus_pizza_meal_2", "campus_pizza_id","Campus Pizza", setOf(DietaryOption.GF), setOf(DayOfWeek.TUESDAY))
    val shawarmaPlusMeal =  Meal("shawarma_plus_meal_1", "shawarma_plus_id","Shawarma Plus", setOf(DietaryOption.HALAL, DietaryOption.MEAT), setOf(DayOfWeek.SUNDAY))
    val shawarmaPlusMeal2 =  Meal("shawarma_plus_meal_2", "shawarma_plus_id","Shawarma Plus", setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL), setOf(DayOfWeek.WEDNESDAY))

    val allMeals = setOf(lazeezMeal, lazeezMeal2, campusPizzaMeal, campusPizzaMeal2, shawarmaPlusMeal, shawarmaPlusMeal2)
    val pickUpOptions = setOf(DayOfWeek.TUESDAY)
}