package com.example.munchbox.data

import com.example.munchbox.controller.Restaurant
import com.google.firebase.firestore.FirebaseFirestore


data class MuncherUiState (
    var availableRestaurants : Set<Restaurant> = setOf(),
    var orderUiState: OrderUiState = OrderUiState(),
    val storageServices : StorageServices = StorageServices(FirebaseFirestore.getInstance())
)

data class RestaurantUiState (
    var restaurant : Restaurant = Restaurant(),
    val storageServices : StorageServices = StorageServices(FirebaseFirestore.getInstance())
)