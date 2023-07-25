package com.example.munchbox.data

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class StorageServices
@Inject
constructor(private val firestore: FirebaseFirestore){
    fun restaurantService(): RestaurantStorageService{
        return RestaurantStorageService(firestore)
    }
    fun orderService(): OrderStorageService{
        return OrderStorageService(firestore)
    }

    fun userService() : UserStorageService {
        return UserStorageService(firestore)
    }

}
