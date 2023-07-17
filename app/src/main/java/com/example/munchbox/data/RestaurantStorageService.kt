package com.example.munchbox.data

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

// TODO: replace all List<Meal> with List<String> once meal IDs are implemented
class RestaurantStorageService
@Inject
constructor(private val firestore: FirebaseFirestore){
    suspend fun createDBRestaurant(name : String, imageID: Int? = null): String? = withContext(Dispatchers.IO) {
        val restaurantID:String = "restaurant_" + UUID.randomUUID().toString()
        val meals : List<Meal> = listOf()
        val data = hashMapOf(
            "restaurantID" to restaurantID,
            "name" to name,
            "meals" to meals,
            "imageID" to imageID
        )
        try {
            firestore.collection("Restaurants").document(restaurantID).set(data)
            return@withContext restaurantID
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null

    }

    suspend fun getRestaurantByID(restaurantID: String): Restaurant? = withContext(Dispatchers.IO){
        val restaurantSnapshot = firestore.collection("Restaurants").document(restaurantID).get().await()
        try {
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

    suspend fun getAllRestaurants(): List<Restaurant>? = withContext(Dispatchers.IO){
        var restaurants:MutableList<Restaurant> = mutableListOf()
        val restaurantSnapshots = firestore.collection("Restaurants").get().await()
        try {
            for(restaurantSnapshot in restaurantSnapshots) {
                if(restaurantSnapshot.exists()){
                    val data = restaurantSnapshot.data

                    // Update the fields of the restaurant object with Firestore data
                    val id = data?.get("restaurantID") as? String ?: ""
                    val name = data?.get("name") as? String ?: ""

                    val mealsList = data?.get("meals") as? List<Meal> ?: listOf()
                    val meals = mealsList.toMutableSet()

                    val imageID = data?.get("imageID") as? Int

                    restaurants.add(Restaurant(id,name,meals,imageID))
                }
            }

            return@withContext restaurants
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun updateRestaurantByID(restaurantID: String, name : String? = null, meals: Set<Meal>? = null, imageID: Int? = null): String? = withContext(Dispatchers.IO){
        val data:MutableMap<String, Any?> = mutableMapOf()

        if(name != null) {
            data["name"] = name
        }

        if(meals != null) {
            val listMeals : List<Meal> = meals.toList()
            data["meals"] = meals
        }

        if(imageID != null) {
            data["imageID"] = imageID
        }
        try{
            firestore.collection("Restaurants").document(restaurantID).set(data, SetOptions.merge())
            return@withContext restaurantID
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun deleteRestaurantByID(restaurantID: String): Boolean= withContext(Dispatchers.IO) {
        try {
            firestore.collection("Restaurants").document(restaurantID).delete()
            return@withContext true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext false
    }
}
