package com.example.munchbox.data

import android.util.Log
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal

/**
 * Data class that represents the current UI state in terms of [currentOrderQuantity], [flavor],
 * [dateOptions], selected pickup [date] and [currentOrderPrice]
 */
// TODO: Probably delete pickupOptions and change Meals to Orders
data class OrderUiState(
    //TODO: potentialy change this to a set of orders instead(GIANT OVERHAUL DAY)
    var meals: List<Meal> = listOf(), //TODO: rename to orderedMeals
    /** Selected meal quantity (1, 3, 5) */
    var currentOrderQuantity: Int = 0,
    /** Total price for the order */
    var currentOrderPrice: String = "",
    /** Available pickup dates for an order*/
    /** mapped as the mealID -> Day to pick up*/

    //TODO: currently if the same order is made from the same meal on the meal selection
    // it views them as one order. Potential issue due to them having the same mealID
    var selectedToPickUpDay: Map<Meal, DayOfWeek> = mutableMapOf(), //TODO: rename to orderedSelectedPickupDay

    var unorderedMeals : List<Meal> = listOf(),
    var unorderedSelectedPickupDay : MutableMap<Meal, DayOfWeek> = mutableMapOf()
)
{
    fun addOrderedMeal(newMeal: Meal?, day : DayOfWeek) {
        if (newMeal != null) {
            meals = meals.plus(newMeal)
            selectedToPickUpDay = selectedToPickUpDay.plus(Pair(newMeal, day))
            Log.d("HELLO ORDERUISTATE", newMeal.mealID)
        }
    }
}