/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.munchbox.ui

import androidx.lifecycle.ViewModel
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Order
import com.example.munchbox.controller.Restaurant
import com.example.munchbox.data.DataSource.currentDay
import com.example.munchbox.data.MuncherUiState
import com.example.munchbox.data.OrderUiState
import com.example.munchbox.data.RestaurantUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Date


/**
 * [OrderViewModel] holds information about a meal plan order
 */
class MuncherViewModel : ViewModel() {
    /**
     * MAIN SCREEN
     *      VIEW MODEL
     *          Orders
     *          Restaurants + Meals
     *          DB --> Restaurants + Meals, Orders
     *      VIEW MODEL.update()
     *
     */
    private val _uiState = MutableStateFlow(MuncherUiState())
    val uiState: StateFlow<MuncherUiState> = _uiState.asStateFlow()

    /**
     * Adds a set of meals to the UI state that are pending payment
     */
    fun setUnorderedMeals(mealDayPairs : MutableMap<Meal, DayOfWeek>, mealList : List<Meal>) {
        _uiState.update { currentState ->
            currentState.copy(
                orderUiState = currentState.orderUiState.copy(
                    unorderedMeals = mealList,
                    unorderedSelectedPickupDay = mealDayPairs
                )
            )
        }
    }
    /**
     * Clear the unordered meals from the UI state
     */
    fun clearUnorderedMeals() {
        _uiState.update { currentState ->
            currentState.copy(
                orderUiState = currentState.orderUiState.copy(
                    unorderedMeals = listOf(),
                    unorderedSelectedPickupDay = mutableMapOf<Meal, DayOfWeek>()
                )
            )
        }
    }
    /**
     * Grab all the ordered orders for a user
     */
    suspend fun getOrders(userId : String) : Set<Order> {
        val order = _uiState.value.storageServices.orderService().getAllOrdersByUserID(userId)
        if (order == null) {
            return setOf<Order>()
        }
        else {
            return order.toSet()
        }
    }

    fun getDayOfWeekFromDate(date: Date): DayOfWeek {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val kotlinDayInt : Int = calendar.get(Calendar.DAY_OF_WEEK)
        return DayOfWeek.values()[kotlinDayInt - 1]
    }


    /**
     * Update the order UI state from DB
     */
    suspend fun getOrderUiState(userId : String): OrderUiState{
        // TODO @SEAN
        var ret : OrderUiState = OrderUiState()
        val orders : Set<Order> = getOrders(userId)
        // Append all meals to the order ui state
        for (order in orders) {
             ret.addOrderedMeal(_uiState.value.storageServices.restaurantService().getMeal(order.restaurantID, order.mealID),
                 getDayOfWeekFromDate(order.pickUpDate))
        }
        return ret
    }

    suspend fun updateConfirmedMeals(meals: List<Meal>, pickupOptions: MutableMap<Meal, DayOfWeek>){
        for((meal, date) in pickupOptions){
            if(date == currentDay){
                _uiState.value.storageServices.orderService().deleteOrderByMealID(meal.mealID)
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                orderUiState = currentState.orderUiState.copy(
                    // TODO:replace current day with actual current day
                    meals = meals.filter{meal -> pickupOptions[meal]!=currentDay}
                )
            )
        }
    }

    /**
     * Grab all available restaurants from DB
     */
    suspend fun updateRestaurants() : Set<Restaurant> {
        val restaurants = _uiState.value.storageServices.restaurantService().getAllRestaurants()
        if (restaurants == null) {
            return setOf<Restaurant>()
        }
        else {
            return restaurants.toSet()
        }
    }
//    maybe not needed?
    suspend fun updateMuncherState (userId : String) {
        _uiState.update { currentState ->
            currentState.copy(
                orderUiState = getOrderUiState(userId),
                availableRestaurants = updateRestaurants()
            )
        }
    }

    /**
     * Set the quantity [currentOrderQuantity] of orderNum for this order's state and update the price
     */
    fun setQuantity(numberMeal: Int, priceMeal: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                orderUiState = currentState.orderUiState.copy(
                    currentOrderQuantity = numberMeal,
                    currentOrderPrice = "$$priceMeal"
                )
            )
        }
    }
}
class RestaurantViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RestaurantUiState())
    val uiState: StateFlow<RestaurantUiState> = _uiState.asStateFlow()

    /**
     * Grab all available restaurants from DB
     */
    fun updateMeals(userId : String) : Set<Meal> {
        // TODO
        return setOf<Meal>()
    }
    /**
     * Grab all the ordered orders for a user
     */
    fun updateRestaurantState (userId : String) {
        _uiState.update { currentState ->
            currentState.copy(
                meals = updateMeals(userId),
            )
        }
    }
}
class OrderViewModel : ViewModel() {

    /**
     * Cupcake state for this order
     */
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    /**
     * Set the quantity [numberCupcakes] of cupcakes for this order's state and update the price
     */
    fun setQuantity(numberCupcakes: Int, priceCupcakes: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                currentOrderQuantity = numberCupcakes,
                currentOrderPrice = "$$priceCupcakes"
            )
        }
    }
    /**
     * Set the pickup options for the meals
     */
    fun setPickupOptions(pickupOptions:MutableMap<Meal, DayOfWeek>){
        _uiState.update { currentState ->
            currentState.copy(
                selectedToPickUpDay = pickupOptions,
            )
        }
    }
    /**
     * Set the meals for the order being made
     */
    fun setMeals(meals: List<Meal>){
        _uiState.update { currentState ->
            currentState.copy(
                meals = meals,
            )
        }
    }

    /**
     * Set the [pickupDate] for this order's state and update the price
     */
    // TODO: OrderUIState no longer uses date, it is stored in the Order Collection in DB (can we delete this?)
//    fun setDate(pickupDate: String) {
//        _uiState.update { currentState ->
//            currentState.copy(
//                date = pickupDate,
//            )
//        }
//    }

    /**
     * Reset the order state
     */
    fun resetOrder() {
        _uiState.value = OrderUiState(selectedToPickUpDay = mutableMapOf())
    }

    /**
     * Returns the calculated price based on the order details.
     */
//    private fun calculatePrice(
//        quantity: Int = _uiState.value.quantity,
//        pickupDate: String = _uiState.value.date
//    ): String {
//        var calculatedPrice = quantity * PRICE_PER_CUPCAKE
//        // If the user selected the first option (today) for pickup, add the surcharge
//        if (pickupOptions()[0] == pickupDate) {
//            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
//        }
//        val formattedPrice = NumberFormat.getCurrencyInstance().format(calculatedPrice)
//        return formattedPrice
//    }

    /**
     * Returns a list of date options starting with the current date and the following 3 dates.
     */
}
