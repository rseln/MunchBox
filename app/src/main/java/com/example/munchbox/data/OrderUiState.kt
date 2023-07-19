package com.example.munchbox.data

import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal

/**
 * Data class that represents the current UI state in terms of [quantity], [flavor],
 * [dateOptions], selected pickup [date] and [price]
 */
data class OrderUiState(
    var meals: Set<Meal> = setOf(),
    /** Selected meal quantity (1, 3, 5) */
    val quantity: Int = 0,
    /** Total price for the order */
    val price: String = "",
    /** Available pickup dates for the order*/
    val pickupOptions: Set<DayOfWeek> = setOf(),
)