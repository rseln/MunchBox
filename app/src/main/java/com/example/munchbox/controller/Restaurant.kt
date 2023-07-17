package com.example.munchbox.controller

// TODO: delete addMeal(s) and replace usages with API endpoints
class Restaurant(val restaurantID : String, val name : String, var meals : Set<Meal> = setOf(), val imageID: Int? = null) {

}