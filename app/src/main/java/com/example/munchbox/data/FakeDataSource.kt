package com.example.munchbox.data

import com.example.munchbox.R
import com.example.munchbox.controller.Restaurant

object DataSource {
//    val flavors = listOf(
//        R.string.vanilla,
//        R.string.chocolate,
//        R.string.red_velvet,
//        R.string.salted_caramel,
//        R.string.coffee
//    )

    val quantityOptions = listOf(
        Triple(R.string.meals_1, 1, 10),
        Triple(R.string.meals_3, 3, 27),
        Triple(R.string.meals_5, 5, 42)
    )

    val fakeOrders = listOf(
        OrderUiState(restaurant = Restaurant(name ="Lazeez", imageID = R.drawable.lazeez )),
        OrderUiState(restaurant = Restaurant("Campus Pizza")),
        OrderUiState(restaurant = Restaurant("Shawarama Plus"))
    )
}