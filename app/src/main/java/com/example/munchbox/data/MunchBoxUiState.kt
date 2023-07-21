package com.example.munchbox.data

import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.google.firebase.firestore.FirebaseFirestore

/**
 * MUNCHBOXUISTATE
 *      MUNCHER_UI_STATE
 *          ORDER_UI_STATE
 *      RESTUARNAT_UI_STATE
 *
 */
data class MunchBoxUiState (
    val muncherUiState: MuncherUiState = MuncherUiState(),
    val restaurantUiState: RestaurantUiState = RestaurantUiState()
)

data class MuncherUiState (
    var availableRestaurants : Set<Restaurant> = setOf(),
    var orderUiState: OrderUiState = OrderUiState(),
    val storageServices : StorageServices = StorageServices(FirebaseFirestore.getInstance())
)

data class RestaurantUiState (
    var meals : Set<Meal> = setOf(),
    val storageServices : StorageServices = StorageServices(FirebaseFirestore.getInstance())
)