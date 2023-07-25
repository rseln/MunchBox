package com.example.munchbox.data

import com.example.munchbox.R
import com.example.munchbox.controller.DayOfWeek
import java.util.Calendar
import java.util.Date

fun today(): DayOfWeek {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    val kotlinDayInt : Int = calendar.get(Calendar.DAY_OF_WEEK)
    return DayOfWeek.values()[kotlinDayInt - 1]
}

object DataSource {
    val currentDay = today()
    val quantityOptions = listOf(
        Triple(R.string.meals_1, 1, 10),
        Triple(R.string.meals_3, 3, 27),
        Triple(R.string.meals_5, 5, 42)
    )
}