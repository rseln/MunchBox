package com.example.munchbox.data

import android.util.Log
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Order
import com.example.munchbox.controller.Restaurant
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
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
        pickUpDate:Date,
        orderPickedUp: Boolean
    ): Order? = withContext(Dispatchers.IO) {
        val orderID:String = "order_" + UUID.randomUUID().toString()

        val data = hashMapOf(
            "orderID" to orderID,
            "userID" to userID,
            "restaurantID" to restaurantID,
            "mealID" to mealID,
            "pickUpDate" to Timestamp(pickUpDate),
            "orderPickedUp" to orderPickedUp
        )

        try {
            firestore.collection("Orders").document(orderID).set(data)
            return@withContext Order(orderID, userID, restaurantID, mealID, pickUpDate, orderPickedUp)
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun getOrderByOrderID(orderID:String ): Order? = withContext(Dispatchers.IO) {
        try {
            val orderSnapshot = firestore.collection("Orders").document(orderID).get().await()
            if(orderSnapshot.exists()){
                return@withContext createOrderObject(orderSnapshot)
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun getAllOrders(): List<Order>? = withContext(Dispatchers.IO) {
        val orders: MutableList<Order>  = mutableListOf()
        try {
            val querySnapshot = firestore.collection("Orders").get().await()
            for(orderSnapshot in querySnapshot.documents){
                orders.add(createOrderObject(orderSnapshot))
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }

        return@withContext null
    }

    suspend fun getAllOrdersByUserID(userID: String ): List<Order>? = withContext(Dispatchers.IO) {
        val orders: MutableList<Order>  = mutableListOf()
        try {
            val querySnapshot = firestore.collection("Orders").whereEqualTo("userID", userID).get().await()
            for(orderSnapshot in querySnapshot.documents){
                orders.add( createOrderObject(orderSnapshot))
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }

        return@withContext null
    }

    suspend fun getAllOrdersByRestaurantID(restaurantID: String): List<Order>? = withContext(Dispatchers.IO) {
        val orders: MutableList<Order>  = mutableListOf()
        try {
            val querySnapshot = firestore.collection("Orders").whereEqualTo("restaruantID", restaurantID).get().await()
            for(orderSnapshot in querySnapshot.documents){
                orders.add( createOrderObject(orderSnapshot))
            }
        }catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }

        return@withContext null
    }
    suspend fun updateOrderByOrderID(orderID:String, orderPickedUp: Boolean): String? = withContext(Dispatchers.IO) {
        val data:MutableMap<String, Any?> = mutableMapOf()
        data["orderPickedUp"] = orderPickedUp
        try{
            firestore.collection("Orders").document(orderID).set(data, SetOptions.merge())
            return@withContext orderID
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }
    suspend fun deleteOrderByOrderID(orderID: String): Boolean? = withContext(Dispatchers.IO){
        try {
            firestore.collection("Orders").document(orderID).delete()
            return@withContext true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext false
    }

    private fun createOrderObject(orderSnapshot: DocumentSnapshot): Order{
        val orderData = orderSnapshot.data
        val orderID = orderData?.get("orderID") as? String ?: ""
        val userID = orderData?.get("userID") as? String ?: ""
        val mealID = orderData?.get("mealID") as? String ?: ""
        val restaurantID = orderData?.get("restaurantID") as? String ?: ""
        val fetchedPickUpDate = orderData?.get("pickUpDate") as? Timestamp?: Timestamp(Date())
        val pickUpDate = fetchedPickUpDate.toDate()
        val orderPickedUp = orderData?.get("orderPickedUp") as? Boolean?: false
        return Order(orderID, userID, restaurantID, mealID, pickUpDate, orderPickedUp)
    }
}