package com.example.munchbox.controller

/**
 * @name - Meal
 * @brief - Class interface for a single meal.
 * @member options - Array of Dietary restrictions
 * @member restaurant - Restaurant
 * @member - Day availability
 */
//TODO: remove restaurant object and replace usage with restaurantID with meal APIs
//TODO: remove usage of total order count and replace with totalOrders from API
//TODO: add a cancelled meal field : Date (YY/MM/DD)
//TODO: learn abt references
data class Meal(
    val mealID: String,
    val restaurantID: String,
    val options : Set<DietaryOption>,
    val days : Set<DayOfWeek>,
    val amountOrdered : Map<DayOfWeek, Int> = mapOf(),
    val totalOrders: Int = 0,
)
