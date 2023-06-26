package com.example.munchbox.data

import com.example.munchbox.R
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant

object DataSource {
//    val flavors = listOf(
//        R.string.vanilla,
//        R.string.chocolate,
//        R.string.red_velvet,
//        R.string.salted_caramel,
//        R.string.coffee
//    )
    val currentDay = DayOfWeek.TUESDAY

    val quantityOptions = listOf(
        Pair(R.string.meals_1, 1),
        Pair(R.string.meals_3, 3),
        Pair(R.string.meals_5, 5)
    )
    /** Fake Restaurants to use for now.**/
    val lazeez = Restaurant(name ="Lazeez", imageID = R.drawable.lazeez )
    val campusPizza = Restaurant(name="Campus Pizza", imageID = R.drawable.campuspizza)
    val shawaramaPlus = Restaurant(name="Shawarama Plus", imageID = R.drawable.shawarmaplus)

    val lazeezMeal = Meal(setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL, DietaryOption.MEAT), lazeez, setOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))
    val campusPizzaMeal =  Meal(setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL, DietaryOption.MEAT), campusPizza, setOf(DayOfWeek.SUNDAY, DayOfWeek.TUESDAY))
    val shawarmaPlusMeal =  Meal(setOf(DietaryOption.VEGE, DietaryOption.GF, DietaryOption.HALAL, DietaryOption.MEAT), shawaramaPlus, setOf(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
    val fakeOrders = listOf(
        OrderUiState(meals = setOf(lazeezMeal), pickupOptions = setOf(DayOfWeek.TUESDAY)),
        OrderUiState(meals = setOf(campusPizzaMeal)),
        OrderUiState(meals = setOf(shawarmaPlusMeal)),
    )
}