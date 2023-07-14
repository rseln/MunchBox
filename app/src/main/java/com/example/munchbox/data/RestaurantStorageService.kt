package com.example.munchbox.data

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject


class RestaurantStorageService
@Inject
constructor(private val firestore: FirebaseFirestore){

    fun createDBRestaurant(name : String, imageID: Int? = null): String {
        val restaurantID:String = name.lowercase().replace(" ", "_") + UUID.randomUUID().toString()
        val firestore = Firebase.firestore

        val meals : List<Meal> = listOf()
        val data = hashMapOf(
            "restaurantID" to restaurantID,
            "name" to name,
            "meals" to meals,
            "imageID" to imageID
        )

        firestore.collection("Restaurants").document(restaurantID)
            .set(data)
            .addOnSuccessListener { }
            .addOnFailureListener { }

        return restaurantID
    }

    suspend fun getRestaurantByID(restaurantID: String): Restaurant? = withContext(Dispatchers.IO){
        val restaurantSnapshot = firestore.collection("Restaurants").document(restaurantID).get().await()
        try{
            if(restaurantSnapshot.exists()){
                val data = restaurantSnapshot.data

                // Update the fields of the restaurant object with Firestore data
                val id = data?.get("restaurantID") as? String ?: ""
                val name = data?.get("name") as? String ?: ""

                val mealsList = data?.get("meals") as? List<Meal> ?: listOf()
                val meals = mealsList.toMutableSet()

                val imageID = data?.get("imageID") as? Int

                return@withContext Restaurant(id,name,meals,imageID)
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }
}

//suspend fun getRestaurantData(restaurantID : String): Restaurant? {
//    val firestore = Firebase.firestore
//    val docRef = firestore.collection("Restaurants").document(restaurantID)
//
//    var restaurant : Restaurant ? = null // Create an instance of the Restaurant class
//
//    docRef.get()
//        .addOnSuccessListener { document ->
//            if (document.exists()) {
//                // Access the document data as a Map
//                val data = document.data
//
//                // Update the fields of the restaurant object with Firestore data
//                val id = data?.get("restaurantID") as? String ?: ""
//                val name = data?.get("name") as? String ?: ""
//
//                val mealsList = data?.get("meals") as? List<Meal> ?: listOf()
//                val meals = mealsList.toMutableSet()
//
//                val imageID = data?.get("imageID") as? Int
//
//                restaurant = Restaurant(id, name, meals, imageID)
//                Log.d("RESTAURANT NAME", restaurant!!.name)
//            }
//        }
//        .addOnFailureListener { exception ->
//        }
//
//    Log.d("RESTAURANT NAME2", "IM HERE")
//
//    return restaurant
//}