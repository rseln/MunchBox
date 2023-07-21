package com.example.munchbox.controller

import java.util.Calendar
import java.util.Date

/**
 * ENUM DEFINITIONS
 */

// The dayOfWeek val is different from id due to calendar using a different id system
enum class DayOfWeek(val id:Int, val date:Date, val str: String) {
    SUNDAY  (0, getNextWeeksDayOfWeek(1), "Sunday"),
    MONDAY (1, getNextWeeksDayOfWeek(2),"Monday"),
    TUESDAY (2, getNextWeeksDayOfWeek(3),"Tuesday"),
    WEDNESDAY (3, getNextWeeksDayOfWeek(4),"Wednesday"),
    THURSDAY (4, getNextWeeksDayOfWeek(5),"Thursday"),
    FRIDAY (5, getNextWeeksDayOfWeek(6),"Friday"),
    SATURDAY (6, getNextWeeksDayOfWeek(7),"Saturday"),
}

enum class DietaryOption(val id: Int, val str : String) {
    HALAL(0, "Halal"),
    VEGE(1, "Vegetarian"),
    KOSHER(2, "Kosher"),
    VEGAN(3, "Vegan"),
    GF(4, "Gluten Free"),
    PEANUT_FREE(5, "Peanut Free"),
    MEAT(6, "Meat")
}
/**
 * END ENUM DEFINITIONS
 */


// This function calculates the given day's next week date based on the current week
// Example: If today is Fri, 21st July the current week from Sun to Sat is the 16th->22nd
//          and I pass in Wednesday(4) to the function, it will return Wed, 26th July
//
fun getNextWeeksDayOfWeek(dayOfWeek:Int):Date{
    val currentDate = Calendar.getInstance()

    // Calculate the current day of the week (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
    val currentDayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)

    // Calculate the number of days to add to reach the next occurrence of the desired day of the week
    val daysToAdd = if (currentDayOfWeek < dayOfWeek) {
        dayOfWeek - currentDayOfWeek
    } else {
        dayOfWeek - currentDayOfWeek + 7
    }

    // Add the number of days to get to the desired day of the current week
    currentDate.add(Calendar.DAY_OF_YEAR, daysToAdd)

    return currentDate.time
}