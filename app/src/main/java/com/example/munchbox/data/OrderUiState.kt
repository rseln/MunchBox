package com.example.munchbox.data

import com.example.munchbox.controller.Restaurant

/**
 * Data class that represents the current UI state in terms of [quantity], [flavor],
 * [dateOptions], selected pickup [date] and [price]
 */
data class OrderUiState(
    val restaurant: Restaurant = Restaurant(""),
    /** Selected meal quantity (1, 6, 12) */
    val quantity: Int = 0,
    /** Selected date for pickup (such as "Jan 1") */
    val date: String = "",
    /** Total price for the order */
    val price: String = "",
    /** Available pickup dates for the order*/
    val pickupOptions: List<String> = listOf()
)