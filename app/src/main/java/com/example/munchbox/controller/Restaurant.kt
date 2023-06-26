package com.example.munchbox.controller

class Restaurant(val name : String, var meals : Set<Meal>) {
    fun addMeal(meal : Meal) {
        meals = meals.plus(meal)
    }
    fun addMeals(newMeals : Set<Meal>) {
        meals = meals.plus(newMeals)
    }
}