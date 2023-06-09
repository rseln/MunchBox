package com.example.munchbox.controller

import com.example.munchbox.controller.Restaurant

/**
 * ENUM DEFINITIONS
 */
enum class DayOfWeek(val id: Int, val str: String) {
    MONDAY (0, "Monday"),
    TUESDAY (1, "Tuesday"),
    WEDNESDAY (2, "Wednesday"),
    THURSDAY (3, "Thursday"),
    FRIDAY (4, "Friday"),
    SATURDAY (5, "Saturday"),
    SUNDAY  (6, "Sunday"),
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

/**
 * @name - Meal
 * @brief - Class interface for a single meal.
 * @member options - Array of Dietary restrictions
 * @member restaurant - Restaurant
 * @member - Day availability
 */
class Meal(val options : Set<DietaryOption>, val restaurant : Restaurant, val days : Set<DayOfWeek>) {

    fun optionToStr(option : DietaryOption): String {
        return option.str
    }
}