package com.example.munchbox.data

import android.util.Log
import com.example.munchbox.controller.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserStorageService
//@Inject

/*constructor(private val firestore: FirebaseFirestore) {

    suspend fun createDBUser (
        userID:String,
        email:String,
        type:String,
        restaurantID: String
    ): User? = withContext(Dispatchers.IO) {

        val userID:String = Firebase.auth.currentUser.uid.String()

        val data = hashMapOf(
            "userID" to userID,
            "email" to email,
            "type" to type,
            "restaurantID" to restaurantID
        )

        try {
            firestore.collection("Users").document(userID).set(data)
            return@withContext User(userID, email, type, restaurantID)
        }catch(e: FirebaseFirestoreException) {
            Log.e("FIRESTORE ERROR", e.message.toString())
        }

    }
    return@withContext null

}*/

