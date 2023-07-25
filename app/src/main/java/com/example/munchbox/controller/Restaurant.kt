package com.example.munchbox.controller

data class Restaurant(
    val restaurantID : String = "0",
    val name : String = "0",
    var meals : Set<Meal> = setOf(),
    val imageID: String? = null
) {
    fun addMeals(newMeals : Set<Meal>) {
        meals = meals.plus(newMeals)
    }
}