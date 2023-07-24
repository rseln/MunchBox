package com.example.munchbox.data

import android.util.Log
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Order

/**
 * Data class that represents the current UI state in terms of [currentOrderQuantity], [flavor],
 * [dateOptions], selected pickup [date] and [currentOrderPrice]
 */
// TODO: Probably delete pickupOptions and change Meals to Orders
data class OrderUiState(
    var meals: List<Meal> = listOf(), //TODO: rename to orderedMeals
    /** Selected meal quantity (1, 3, 5) */
    var currentOrderQuantity: Int = 0,
    /** Total price for the order */
    var currentOrderPrice: String = "",
    /** Available pickup dates for an order*/
    /** mapped as the mealID -> Day to pick up*/

    //TODO: currently if the same order is made from the same meal on the meal selection
    // it views them as one order. Issue caused due to two identical meals being mapped to one value
    // potential solution could be to add the orderID to the Meal DB model
    // Or we get rid of the meal after it is selected in the meal selection screen(encourages eating healthier options?)
    var orderToPickupDay: Map<Order, DayOfWeek> = mutableMapOf(), //TODO: rename to orderedSelectedPickupDay

    var unorderedMeals : List<Meal> = listOf(),
    var unorderedSelectedPickupDay : MutableMap<Meal, DayOfWeek> = mutableMapOf(),
    var orders: Set<Order> = setOf(),
    var orderToMeal: Map<Order, Meal> = mutableMapOf()
)
{
    fun addOrderedMeal(order: Order, newMeal: Meal?, day : DayOfWeek) {
        if (newMeal != null) {
            meals = meals.plus(newMeal)
            orderToPickupDay = orderToPickupDay.plus(Pair(order, day))
            orderToMeal = orderToMeal.plus(Pair(order, newMeal))
            orders = orders.plus(order)
            Log.d("HELLO ORDERUISTATE1", newMeal.mealID)
            Log.d("HELLO ORDERUISTATE1", order.orderID)
        }
    }
}