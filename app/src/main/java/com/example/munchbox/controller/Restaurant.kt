package com.example.munchbox.controller

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