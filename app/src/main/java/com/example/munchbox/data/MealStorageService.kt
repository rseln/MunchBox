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
    fun createDBMeal(restaurantID: String,
                     options : Set<DietaryOption>,
                     days : Set<DayOfWeek>,
                     orders : Map<DayOfWeek, Int> = mapOf()): String
    {
        val mealID:String = "meal_" + UUID.randomUUID().toString()
        val data = hashMapOf(
            "mealID" to mealID,
            "restaurantID" to restaurantID,
            "dietaryOptions" to options.map{it.str},
            "daysOffered" to days.map{it.str},
            "orders" to orders.mapKeys{it.key.str},
            "totalOrders" to totalOrderCount(orders)
        )
        firestore.collection("Meals").document(mealID).set(data)
        return mealID
    }

    suspend fun getMealByID(mealID: String): Meal? = withContext(Dispatchers.IO){
        val mealSnapshot = firestore.collection("Meals").document(mealID).get().await()
        try {
            if(mealSnapshot.exists()){
                val data = mealSnapshot.data

                // Update the fields of the restaurant object with Firestore data
                val mealID = data?.get("mealID") as? String ?: ""
                val restaurantID = data?.get("restaurantID") as? String ?: ""
                val fetchedDietaryOptions = data?.get("DietaryOptions") as? Set<String> ?: setOf()
                val fetchedDaysOffered = data?.get("daysOffered") as? Set<String> ?: setOf()
                val fetchedOrders = data?.get("orders") as? Map<String, Int> ?: mapOf()
                val totalOrders = data?.get("totalOrders") as? Int ?: 0

                val dietaryOptions = fetchedDietaryOptions.map { enumValueOf<DietaryOption>(it) }.toSet()
                val daysOffered = fetchedDaysOffered.map { enumValueOf<DayOfWeek>(it) }.toSet()
                val orders = fetchedOrders.mapKeys { enumValueOf<DayOfWeek>(it.key) }

                return@withContext Meal(mealID, restaurantID, dietaryOptions, daysOffered, orders, totalOrders)
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
                    val fetchedDietaryOptions = data?.get("DietaryOptions") as? Set<String> ?: setOf()
                    val fetchedDaysOffered = data?.get("daysOffered") as? Set<String> ?: setOf()
                    val fetchedOrders = data?.get("orders") as? Map<String, Int> ?: mapOf()
                    val totalOrders = data?.get("totalOrders") as? Int ?: 0

                    val dietaryOptions = fetchedDietaryOptions.map { enumValueOf<DietaryOption>(it) }.toSet()
                    val daysOffered = fetchedDaysOffered.map { enumValueOf<DayOfWeek>(it) }.toSet()
                    val orders = fetchedOrders.mapKeys { enumValueOf<DayOfWeek>(it.key) }
                    meals.add(Meal(mealID, restaurantID, dietaryOptions, daysOffered, orders, totalOrders))
                }
            }
            return@withContext meals

        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    fun updateMealByID(
        mealID: String,
        options: Set<DietaryOption>? = null,
        days: Set<DayOfWeek>? = null,
        orders: Map<DayOfWeek, Int>? = null,
    ): String {
        val data:MutableMap<String, Any?> = mutableMapOf()

        data["dietaryOptions"] = options?.map{it.str} ?: listOf<String>()
        data["daysOffered"] = days?.map{it.str} ?: listOf<String>()
        if (orders != null){
            data["orders"] = orders.mapKeys{it.key.str}
            data["totalOrders"] = totalOrderCount(orders)
        }
        firestore.collection("Meals").document(mealID).set(data, SetOptions.merge())

        return mealID
    }

    fun deleteMealByMealID(mealID: String): Boolean {
        try {
            firestore.collection("Meals").document(mealID).delete()
            return true
        } catch(e: FirebaseFirestoreException){
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return false
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