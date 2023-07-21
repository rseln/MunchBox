package com.example.munchbox.data

import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal

/**
 * Data class that represents the current UI state in terms of [currentOrderQuantity], [flavor],
 * [dateOptions], selected pickup [date] and [currentOrderPrice]
 */
// TODO: Probably delete pickupOptions and change Meals to Orders
data class OrderUiState(
    //TODO: potentialy change this to a set of orders instead(GIANT OVERHAUL DAY)
    var meals: Set<Meal> = setOf(),
    /** Selected meal quantity (1, 3, 5) */
    var currentOrderQuantity: Int = 0,
    /** Total price for the order */
    var currentOrderPrice: String = "",
    /** Available pickup dates for an order*/
    /** mapped as the mealID -> Day to pick up*/
    //TODO: remove this once we have a set of orders then we can use that to keep track of
    // all the pick up dates for the selected values?
    // map of <MEAL, DayOfWeek>
    var selectedToPickUpDay: MutableMap<Meal, DayOfWeek> = mutableMapOf()
)
{
    fun addOrderedMeal(newMeal: Meal?, day : DayOfWeek) {
        if (newMeal != null) {
            meals = meals.plus(newMeal)
            selectedToPickUpDay.plus(Pair(newMeal, day))
        }
    }
}