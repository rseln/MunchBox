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
    val options : Set<DietaryOption>,
    val days : Set<DayOfWeek>,
    val amountOrdered : Map<DayOfWeek, Int> = mapOf(),
    val totalOrders: Int = 0,
) {
    fun totalOrderCount() : Int {
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
