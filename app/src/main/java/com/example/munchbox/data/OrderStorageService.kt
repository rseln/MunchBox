package com.example.munchbox.data

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class OrderStorageService
@Inject
constructor(private val firestore: FirebaseFirestore){

}