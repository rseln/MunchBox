package com.example.munchbox.data

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

// TODO: replace all List<Meal> with List<String> once meal IDs are implemented
class StorageServices
@Inject
constructor(private val firestore: FirebaseFirestore){

}
