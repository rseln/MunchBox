package com.example.munchbox.controller

class Restaurant(val name : String, val imageID: Int? = null) {
    val meals : MutableList<Meal> = mutableListOf()

    fun addMeal(meal : Meal) {
        meals.add(meal)
    }
}