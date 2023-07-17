package com.example.munchbox.data

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
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
        val data = hashMapOf(
            "restaurantID" to restaurantID,
            "name" to name,
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


                val imageID = data?.get("imageID") as? Int

                return@withContext Restaurant(id,name,imageID)
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun getAllRestaurants(): Map<Restaurant, List<Meal>> = withContext(Dispatchers.IO){
        var restaurantsToMeals:MutableMap<Restaurant, List<Meal>> = mutableMapOf()
        val restaurantSnapshots = firestore.collection("Restaurants").get().await()
        try {
            for(restaurantSnapshot in restaurantSnapshots) {
                if(restaurantSnapshot.exists()){
                    val data = restaurantSnapshot.data

                    // Update the fields of the restaurant object with Firestore data
                    val id = data?.get("restaurantID") as? String ?: ""
                    val name = data?.get("name") as? String ?: ""

                    val imageID = data?.get("imageID") as? Int

                    // Create the list of meals from the subcollection
                    var meals:MutableList<Meal> = mutableListOf()
                    val mealsSnapshot = firestore.collection("Restaurants").document(id).collection("Meals").get().await()
                    for(mealSnapshot in mealsSnapshot){
                        if(mealSnapshot.exists()){
                            val mealData = mealSnapshot.data

                            // Update the fields of the restaurant object with Firestore data
                            val mealID = mealData?.get("mealID") as? String ?: ""
                            val restaurantID = mealData?.get("restaurantID") as? String ?: ""
                            val fetchedDietaryOptions = mealData?.get("DietaryOptions") as? Set<String> ?: setOf()
                            val fetchedDaysOffered = mealData?.get("daysOffered") as? Set<String> ?: setOf()
                            val fetchedAmountOrdered = mealData?.get("amountOrdered") as? Map<String, Int> ?: mapOf()
                            val totalOrders = mealData?.get("totalOrders") as? Int ?: 0

                            val dietaryOptions = fetchedDietaryOptions.map { DietaryOption.valueOf(it) }.toSet()
                            val daysOffered = fetchedDaysOffered.map { DayOfWeek.valueOf(it) }.toSet()
                            val orders = fetchedAmountOrdered.mapKeys { DayOfWeek.valueOf(it.key.uppercase()) }
                            meals.add(Meal(mealID, restaurantID, dietaryOptions, daysOffered, orders, totalOrders))
                        }
                    }
                    restaurantsToMeals[Restaurant(id,name,imageID)] = meals
                }
            }
            return@withContext restaurantsToMeals
        }
        catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
    }

    suspend fun updateRestaurantByID(restaurantID: String, name : String? = null, existingMeals: Set<Meal>? = null, imageID: Int? = null): String? = withContext(Dispatchers.IO){
        val data:MutableMap<String, Any?> = mutableMapOf()

        if(name != null) {
            data["name"] = name
        }

        if(imageID != null) {
            data["imageID"] = imageID
        }
        try{
            firestore.collection("Restaurants").document(restaurantID).set(data, SetOptions.merge())

            if(existingMeals != null) {
                for(meal in existingMeals) {
                    val mealData = hashMapOf(
                        "mealID" to meal.mealID,
                        "restaurantID" to meal.restaurantID,
                        "dietaryOptions" to meal.options.map{it.str},
                        "daysOffered" to meal.days.map{it.str},
                        "amountOrdered" to meal.amountOrdered.mapKeys{it.key.str},
                        "totalOrders" to totalOrderCount(meal.amountOrdered)
                    )
                    firestore.collection("Restaurants").document(restaurantID).collection("Meals").document(meal.mealID).set(mealData)
                }
            }

            return@withContext restaurantID
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun addMealToRestaurant(
        restaurantID: String,
        options : Set<DietaryOption> = setOf(),
        days : Set<DayOfWeek> = setOf(),
        amountOrdered : Map<DayOfWeek, Int> = mapOf()
    ): String = withContext(Dispatchers.IO) {

        val mealID:String = "meal_" + UUID.randomUUID().toString()

        val data = hashMapOf(
            "mealID" to mealID,
            "restaurantID" to restaurantID,
            "dietaryOptions" to options.map{it.str},
            "daysOffered" to days.map{it.str},
            "amountOrdered" to amountOrdered.mapKeys{it.key.str},
            "totalOrders" to totalOrderCount(amountOrdered)
        )
        firestore.collection("Restaurants").document(restaurantID).collection("Meals").document(mealID).set(data)

        return@withContext mealID
    }

    suspend fun deleteMeal(restaurantID: String, mealID: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firestore.collection("Restaurants").document(restaurantID).collection("Meals").document(mealID).delete()
            return@withContext true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext false
    }

    private fun totalOrderCount(amountOrdered: Map<DayOfWeek, Int>) : Int {
        var sum : Int = 0;
        for (order in amountOrdered) {
            sum += order.value
        }
        return sum
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
