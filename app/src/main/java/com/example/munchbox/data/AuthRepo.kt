package com.example.munchbox.data

import com.google.firebase.auth.AuthCredential
import com.example.munchbox.util.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow


interface AuthRepo {

    fun loginUser(email:String, password:String): Flow<Resource<AuthResult>>
    fun registerUser(email:String, password:String): Flow<Resource<AuthResult>>
    fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>
}