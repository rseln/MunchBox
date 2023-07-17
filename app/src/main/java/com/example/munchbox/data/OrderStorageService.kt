package com.example.munchbox.data

import android.util.Log
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Order
import com.example.munchbox.controller.Restaurant
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class OrderStorageService
@Inject
constructor(private val firestore: FirebaseFirestore){
    suspend fun createDBOrder(
        userID:String,
        restaurantID: String,
        mealID: String ,
        pickUpDate:Date
    ): Order? = withContext(Dispatchers.IO) {
        val orderID:String = "order_" + UUID.randomUUID().toString()

        val data = hashMapOf(
            "orderID" to orderID,
            "userID" to userID,
            "restaurantID" to restaurantID,
            "mealID" to mealID,
            "pickUpDate" to Timestamp(pickUpDate),
        )

        try {
            firestore.collection("Orders").document(orderID).set(data)
            return@withContext Order(orderID, userID, restaurantID, mealID, pickUpDate)
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun getOrderByOrderId(orderID:String ): Order? = withContext(Dispatchers.IO) {
        try {
            val orderSnapshot = firestore.collection("Orders").document(orderID).get().await()
            if(orderSnapshot.exists()){
                val orderData = orderSnapshot.data

                // Update the fields of the restaurant object with Firestore data
                val orderID = orderData?.get("orderID") as? String ?: ""
                val userID = orderData?.get("userID") as? String ?: ""
                val mealID = orderData?.get("mealID") as? String ?: ""
                val restaurantID = orderData?.get("restaurantID") as? String ?: ""
                val fetchedPickUpDate = orderData?.get("pickUpDate") as? Timestamp?: Timestamp(Date())
                val pickUpDate = fetchedPickUpDate.toDate()

                return@withContext Order(orderID, userID, restaurantID, mealID, pickUpDate)
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }
//TODO: figure out if we want to separate the users or make order a subcollection within user
    suspend fun getOrdersByUserId(userID: String ): List<Order>? = withContext(Dispatchers.IO) {
        return@withContext null
    }

    suspend fun deleteOrderByOrderId(orderID: String): Boolean? = withContext(Dispatchers.IO){
        try {
            firestore.collection("Orders").document(orderID).delete()
            return@withContext true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext false
    }

}