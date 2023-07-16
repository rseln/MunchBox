package com.example.munchbox.data

import android.util.Log
import com.example.munchbox.controller.DayOfWeek
import com.example.munchbox.controller.DietaryOption
import com.example.munchbox.controller.Meal
import com.example.munchbox.controller.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

//TODO: store total amount of orders per meal
//TODO: Maybe change enums into just strings?
class MealStorageService
@Inject
constructor(private val firestore: FirebaseFirestore){
    suspend fun createDBMeal(
        restaurantID: String,
        restaurantName: String,
        options : Set<DietaryOption> = setOf(),
        days : Set<DayOfWeek> = setOf(),
        orders : Map<DayOfWeek, Int> = mapOf()
    ): String = withContext(Dispatchers.IO) {

        val mealID:String = "meal_" + UUID.randomUUID().toString()

        val data = hashMapOf(
            "mealID" to mealID,
            "restaurantID" to restaurantID,
            "restaurantName" to restaurantName,
            "dietaryOptions" to options.map{it.str},
            "daysOffered" to days.map{it.str},
            "orders" to orders.mapKeys{it.key.str},
            "totalOrders" to totalOrderCount(orders)
        )
        firestore.collection("Meals").document(mealID).set(data)

        return@withContext mealID
    }

    suspend fun getMealByID(mealID: String): Meal? = withContext(Dispatchers.IO){
        val mealSnapshot = firestore.collection("Meals").document(mealID).get().await()
        try {
            if(mealSnapshot.exists()){
                val data = mealSnapshot.data

                // Update the fields of the restaurant object with Firestore data
                val mealID = data?.get("mealID") as? String ?: ""
                val restaurantID = data?.get("restaurantID") as? String ?: ""
                val restaurantName = data?.get("restaurantName") as? String ?: ""
                val fetchedDietaryOptions = data?.get("DietaryOptions") as? Set<String> ?: setOf()
                val fetchedDaysOffered = data?.get("daysOffered") as? Set<String> ?: setOf()
                val fetchedOrders = data?.get("orders") as? Map<String, Int> ?: mapOf()
                val totalOrders = data?.get("totalOrders") as? Int ?: 0

                val dietaryOptions = fetchedDietaryOptions.map { DietaryOption.valueOf(it) }.toSet()
                val daysOffered = fetchedDaysOffered.map { DayOfWeek.valueOf(it) }.toSet()
                val orders = fetchedOrders.mapKeys { DayOfWeek.valueOf(it.key.uppercase()) }

                return@withContext Meal(mealID, restaurantID, restaurantName, dietaryOptions, daysOffered, orders, totalOrders)
            }
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun getAllMeals(): List<Meal>? = withContext(Dispatchers.IO){
        var meals:MutableList<Meal> = mutableListOf()
        val mealsSnapshot = firestore.collection("Meals").get().await()
        try {
            for(mealSnapshot in mealsSnapshot){
                if(mealSnapshot.exists()){
                    val data = mealSnapshot.data

                    // Update the fields of the restaurant object with Firestore data
                    val mealID = data?.get("mealID") as? String ?: ""
                    val restaurantID = data?.get("restaurantID") as? String ?: ""
                    val restaurantName = data?.get("restaurantName") as? String ?: ""
                    val fetchedDietaryOptions = data?.get("DietaryOptions") as? Set<String> ?: setOf()
                    val fetchedDaysOffered = data?.get("daysOffered") as? Set<String> ?: setOf()
                    val fetchedOrders = data?.get("orders") as? Map<String, Int> ?: mapOf()
                    val totalOrders = data?.get("totalOrders") as? Int ?: 0

                    val dietaryOptions = fetchedDietaryOptions.map { DietaryOption.valueOf(it) }.toSet()
                    val daysOffered = fetchedDaysOffered.map { DayOfWeek.valueOf(it) }.toSet()
                    val orders = fetchedOrders.mapKeys { DayOfWeek.valueOf(it.key.uppercase()) }
                    meals.add(Meal(mealID, restaurantID, restaurantName, dietaryOptions, daysOffered, orders, totalOrders))
                }
            }
            return@withContext meals

        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun updateMealByID(
        mealID: String,
        restaurantName: String? = null,
        options: Set<DietaryOption>? = null,
        days: Set<DayOfWeek>? = null,
        orders: Map<DayOfWeek, Int>? = null,
    ): String? = withContext(Dispatchers.IO) {
        val data:MutableMap<String, Any?> = mutableMapOf()

        if(options != null){
            data["dietaryOptions"] = options.map{it.str}
        }
        if(days != null){
            data["daysOffered"] = days?.map{it.str}
        }
        if(restaurantName!=null){
            data["restaurantName"] = restaurantName
        }
        if (orders != null){
            data["orders"] = orders.mapKeys{it.key.str}
            data["totalOrders"] = totalOrderCount(orders)
        }

        try{
            firestore.collection("Meals").document(mealID).set(data, SetOptions.merge())
            return@withContext mealID
        } catch (e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun deleteMealByMealID(mealID: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firestore.collection("Meals").document(mealID).delete()
            return@withContext true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext false
    }

    suspend fun deleteMealsByRestaurantID(restaurantID: String): Boolean = withContext(Dispatchers.IO) {
        try {

            val querySnapshot = firestore.collection("Meals").whereEqualTo("restaurantID",restaurantID).get().await()
            for(mealSnapshot in querySnapshot.documents){
                mealSnapshot.reference.delete().await()
            }
            return@withContext true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext false
    }

    private fun totalOrderCount(orders: Map<DayOfWeek, Int>) : Int {
        var sum : Int = 0;
        for (order in orders) {
            sum += order.value
        }
        return sum
    }
}