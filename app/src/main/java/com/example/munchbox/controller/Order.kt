package com.example.munchbox.controller

import java.util.Date

data class Order(
    val orderID: String,
    val restaurantID: String,
    val mealID: String,
    val pickUpDate: Date
)
