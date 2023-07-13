package com.example.munchbox.controller


class Restaurant(val restaurantID : String, val name : String, var meals : Set<Meal> = setOf(), val imageID: Int? = null) {
    fun addMeal(meal : Meal) {
        meals = meals.plus(meal)
    }
    fun addMeals(newMeals : Set<Meal>) {
        meals = meals.plus(newMeals)
    }
}