
package com.example.munchbox.data

import android.util.Log
import com.example.munchbox.controller.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class UserStorageService

@Inject

constructor(private val firestore: FirebaseFirestore) {
    suspend fun createDBUser (
        userID:String,
        email:String,
        type:String,
        restaurantID: String? = null
    ): User? = withContext(Dispatchers.IO) {

        val userID:String = Firebase.auth.currentUser?.uid ?: ""

        val data = hashMapOf(
            "userID" to userID,
            "email" to email,
            "type" to type,
            "restaurantID" to restaurantID
        )

        try {
            firestore.collection("Users").document(userID).set(data)
            return@withContext User(userID, email, type, restaurantID)
        } catch(e: FirebaseFirestoreException) {
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null

    }

    suspend fun getUserByUserID(userID:String): User? = withContext(Dispatchers.IO) {
        try {
            val userSnapshot = firestore.collection("Users").document(userID).get().await()
            if(userSnapshot.exists()) {
                val data = userSnapshot.data
                val id = data?.get("userID") as? String ?: ""
                val email = data?.get("email") as? String ?: ""
                val type = data?.get("type") as? String ?: ""
                val restaurantId = data?.get("restaurantID") as? String ?: ""

                return@withContext User(id, email, type, restaurantId)
            }
        } catch(e: FirebaseFirestoreException) {
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

    suspend fun getTypeByUserID(userID:String): String? = withContext(Dispatchers.IO) {
        try {
            val userSnapshot = firestore.collection("Users").document(userID).get().await()
            if(userSnapshot.exists()) {
                val data = userSnapshot.data
                val type = data?.get("type") as? String ?: ""

                return@withContext type
            }
        } catch(e: FirebaseFirestoreException) {
            Log.e("FIRESTORE ERROR", e.message.toString())
        }
        return@withContext null
    }

}


