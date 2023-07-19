package com.example.munchbox.controller

/**
 * @name - Meal
 * @brief - Class interface for a single meal.
 * @member options - Array of Dietary restrictions
 * @member restaurant - Restaurant
 * @member - Day availability
 */
data class Meal(
    val mealID: String,
    val restaurantID: String,
    val dietaryRestrictionsOffered : Set<DietaryOption>,
    val daysMealIsOffered : Set<DayOfWeek>,
    val amountOrdered : Map<DayOfWeek, Int> = mapOf(),
    val totalOrders: Int = 0,
) {
    fun totalOrderCount() : Int { //TODO: replace with totalOrders from api call in db
        var sum : Int = 0;
        for (order in amountOrdered) {
            sum += order.value
        }
        return sum
    }
    fun optionToStr(option : DietaryOption): String {
        return option.str
    }
}
