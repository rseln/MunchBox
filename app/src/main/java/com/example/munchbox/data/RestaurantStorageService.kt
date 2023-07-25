package com.example.munchbox.data

import android.util.Log
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class RestaurantStorageService
@Inject
constructor(private val firestore: FirebaseFirestore){
    // ***********************************
    // RESTAURANT API ENDPOINTS START HERE
    // ***********************************

    val storageServices = StorageServices(FirebaseFirestore.getInstance())

    // Stores the Restaurant in the DB, and returns the Restaurant object (no meals initialized)
    suspend fun createDBRestaurant(name : String, imageID: String? = null): Restaurant? = withContext(Dispatchers.IO) {
        val restaurantID:String = "restaurant_" + UUID.randomUUID().toString()
        val data = hashMapOf(
            "restaurantID" to restaurantID,
            "name" to name,
            "imageID" to imageID
        )
        try {
            firestore.collection("Restaurants").document(restaurantID).set(data)
            val user = Firebase.auth.currentUser
            if (user != null) {
                storageServices.userService().updateRestaurantIDByUserID(restaurantID, user.uid)
            }
            return@withContext Restaurant(restaurantID, name, setOf(), imageID)
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    // Returns a single Restaurant object with its meal information filled in Restaurant.meals
    suspend fun getRestaurantByID(restaurantID: String): Restaurant? = withContext(Dispatchers.IO){
        try {
            val restaurantSnapshot = firestore.collection("Restaurants").document(restaurantID).get().await()
            if(restaurantSnapshot.exists()){
                val data = restaurantSnapshot.data

                // Update the fields of the restaurant object with Firestore data
                val id = data?.get("restaurantID") as? String ?: ""
                val name = data?.get("name") as? String ?: ""

                var meals:MutableList<Meal> = mutableListOf()
                val mealsSnapshot = firestore.collection("Restaurants").document(id).collection("Meals").get().await()
                for(mealSnapshot in mealsSnapshot){
                    if(mealSnapshot.exists()){
                        val mealData = mealSnapshot.data

                        // Update the fields of the restaurant object with Firestore data
                        val mealID = mealData?.get("mealID") as? String ?: ""
                        val restaurantID = mealData?.get("restaurantID") as? String ?: ""
                        val fetchedDietaryOptions = mealData?.get("dietaryOptions") as? List<String> ?: setOf()
                        val fetchedDaysOffered = mealData?.get("daysOffered") as? List<String> ?: setOf()
                        val fetchedAmountOrdered = mealData?.get("amountOrdered") as? Map<String, Int> ?: mapOf()
                        val fetchedTotalOrders = mealData?.get("totalOrders") as? Number ?: 0

                        val totalOrders = fetchedTotalOrders.toInt()
                        val dietaryOptions = fetchedDietaryOptions.map { DietaryOption.valueOf(it.replace(" ", "_").uppercase()) }.toSet()
                        val daysOffered = fetchedDaysOffered.map { DayOfWeek.valueOf(it.uppercase()) }.toSet()
                        val amountOrdered = fetchedAmountOrdered.mapKeys { DayOfWeek.valueOf(it.key.uppercase()) }
                        meals.add(Meal(mealID, restaurantID, dietaryOptions, daysOffered, amountOrdered, totalOrders, Date(0)))
                    }
                }
                val imageID = data?.get("imageID") as? String

                return@withContext Restaurant(id,name, meals.toSet(), imageID)
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    // Returns all Restaurant objects with their meal information filled in Restaurant.meals
    suspend fun getAllRestaurants(): List<Restaurant>?= withContext(Dispatchers.IO){
        var restaurants : MutableList<Restaurant> = mutableListOf()
        try {
            val restaurantSnapshots = firestore.collection("Restaurants").get().await()
            for(restaurantSnapshot in restaurantSnapshots) {
                if(restaurantSnapshot.exists()){
                    val data = restaurantSnapshot.data

                    // Update the fields of the restaurant object with Firestore data
                    val id = data?.get("restaurantID") as? String ?: ""
                    val name = data?.get("name") as? String ?: ""

                    val imageID = data?.get("imageID") as? String

                    // Create the list of meals from the subcollection
                    var meals:MutableList<Meal> = mutableListOf()
                    val mealsSnapshot = firestore.collection("Restaurants").document(id).collection("Meals").get().await()
                    for(mealSnapshot in mealsSnapshot){
                        if(mealSnapshot.exists()){
                            val mealData = mealSnapshot.data

                            // Update the fields of the restaurant object with Firestore data
                            val mealID = mealData?.get("mealID") as? String ?: ""
                            val restaurantID = mealData?.get("restaurantID") as? String ?: ""
                            val fetchedDietaryOptions = mealData?.get("dietaryOptions") as? List<String> ?: listOf()
                            val fetchedDaysOffered = mealData?.get("daysOffered") as? List<String> ?: listOf()
                            val fetchedAmountOrdered = mealData?.get("amountOrdered") as? Map<String, Int> ?: mapOf()
                            val fetchedTotalOrders = mealData?.get("totalOrders") as? Number ?: 0

                            val totalOrders = fetchedTotalOrders.toInt()
                            Log.d("HELLO IN GET REST", fetchedDaysOffered[0])

                            val dietaryOptions = fetchedDietaryOptions.map { DietaryOption.valueOf(it.replace(" ", "_").uppercase()) }.toSet()
                            val daysOffered = fetchedDaysOffered.map { DayOfWeek.valueOf(it.uppercase()) }.toSet()
                            for(days in daysOffered){
                                Log.d("HELLO IN GET MEAL2", days.str)
                            }
                            val amountOrdered = fetchedAmountOrdered.mapKeys { DayOfWeek.valueOf(it.key.uppercase()) }
                            meals.add(Meal(mealID, restaurantID, dietaryOptions, daysOffered, amountOrdered, totalOrders, Date(0)))
                        }
                    }
                    restaurants.add(Restaurant(id,name, meals.toSet(), imageID))
                }
            }
            return@withContext restaurants
        }
        catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    // Updates the provided fields on a Restaurant object and returns the restaurantID
    suspend fun updateRestaurantByID(restaurantID: String, name : String? = null, imageID: Int? = null): String? = withContext(Dispatchers.IO){
        val data:MutableMap<String, Any?> = mutableMapOf()

        if(name != null) {
            data["name"] = name
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

    // Deletes the requested Restaurant (and all of its Meals) and returns True of False based on success or failure
    suspend fun deleteRestaurantByID(restaurantID: String): Boolean= withContext(Dispatchers.IO) {
        try {
            firestore.collection("Restaurants").document(restaurantID).delete()
            return@withContext true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext false
    }

    // *****************************
    // MEAL API ENDPOINTS START HERE
    // *****************************

    // Adds the Meal object to the Restaurant and returns the Meal object
    suspend fun addMealToRestaurant(
        restaurantID: String,
        options : Set<DietaryOption> = setOf(),
        days : Set<DayOfWeek> = setOf(),
        amountOrdered : Map<DayOfWeek, Int> = mapOf(
            DayOfWeek.MONDAY to 0,
            DayOfWeek.TUESDAY to 0,
            DayOfWeek.WEDNESDAY to 0,
            DayOfWeek.THURSDAY to 0,
            DayOfWeek.FRIDAY to 0,
            DayOfWeek.SATURDAY to 0,
            DayOfWeek.SUNDAY to 0
        )
    ): Meal? = withContext(Dispatchers.IO) {

        val mealID:String = "meal_" + UUID.randomUUID().toString()
        val totalOrders = totalOrderCount(amountOrdered)
        val cancelledOnDate = Date(0)
        val data = hashMapOf(
            "mealID" to mealID,
            "restaurantID" to restaurantID,
            "dietaryOptions" to options.map{it.str.replace(" ", "_").uppercase()},
            "daysOffered" to days.map{it.str.uppercase()},
            "amountOrdered" to amountOrdered.mapKeys{it.key.str},
            "totalOrders" to totalOrders,
            "cancelledOnDate" to cancelledOnDate
        )
        try {
            firestore.collection("Restaurants").document(restaurantID).collection("Meals").document(mealID).set(data)
            return@withContext Meal(mealID, restaurantID, options, days, amountOrdered, totalOrders, cancelledOnDate)
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    // Returns the requested Meal object
    suspend fun getMeal(restaurantID: String, mealID: String): Meal ? = withContext(Dispatchers.IO) {
        try {
            val mealSnapshot = firestore.collection("Restaurants").document(restaurantID).collection("Meals").document(mealID).get().await()
            if(mealSnapshot.exists()){
                val mealData = mealSnapshot.data

                // Update the fields of the restaurant object with Firestore data
                val mealID = mealData?.get("mealID") as? String ?: ""
                val restaurantID = mealData?.get("restaurantID") as? String ?: ""
                val fetchedDietaryOptions = mealData?.get("dietaryOptions") as? List<String> ?: listOf()
                val fetchedDaysOffered = mealData?.get("daysOffered") as? List<String> ?: listOf()
                val fetchedAmountOrdered = mealData?.get("amountOrdered") as? Map<String, Int> ?: mapOf()
                val fetchedTotalOrders = mealData?.get("totalOrders") as? Number ?: 0

                val totalOrders = fetchedTotalOrders.toInt()
                val dietaryOptions = fetchedDietaryOptions.map { DietaryOption.valueOf(it) }.toSet()
                val daysOffered = fetchedDaysOffered.map { DayOfWeek.valueOf(it) }.toSet()
                Log.d("HELLO IN GET MEAL", fetchedDaysOffered[0])

                for(days in daysOffered){
                    Log.d("HELLO IN GET MEAL2", days.str)
                }
                val amountOrdered = fetchedAmountOrdered.mapKeys { DayOfWeek.valueOf(it.key.uppercase()) }

                return@withContext Meal(mealID, restaurantID, dietaryOptions, daysOffered, amountOrdered, totalOrders, Date(0))
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    // Updates the requested Meal object with the provided fields and returns the mealID
    suspend fun updateMeal(restaurantID: String, mealID: String, options: Set<DietaryOption>? = null, days: Set<DayOfWeek>? = null, cancelledOnDate: Date? = null, amountOrdered: Map<DayOfWeek, Int>? = null, orderCount: Int? = 0): String? = withContext(Dispatchers.IO){
        val data:MutableMap<String, Any?> = mutableMapOf()

        if(options != null){
            data["dietaryOptions"] = options.map{it.str}
        }
        if(days != null){
            data["daysOffered"] = days.map{it.str}
        }
        if(cancelledOnDate != null){
            data["cancelledOnDate"] = cancelledOnDate
        }
        if(amountOrdered != null){
            data["amountOrdered"] = amountOrdered.mapKeys{it.key.str}
            data["totalOrders"] = orderCount
        }
        try{
            firestore.collection("Restaurants").document(restaurantID).collection("Meals").document(mealID).set(data, SetOptions.merge())
            return@withContext mealID
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    // Deletes the requested Meal and returns True of False based on success or failure
    suspend fun deleteMeal(restaurantID: String, mealID: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firestore.collection("Restaurants").document(restaurantID).collection("Meals").document(mealID).delete()
            return@withContext true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext false
    }

    // Private function to keep counts of orders
    private fun totalOrderCount(amountOrdered: Map<DayOfWeek, Int>) : Int {
        var sum : Int = 0;
        for (order in amountOrdered) {
            sum += order.value
        }
        return sum
    }
}
